package com.xebialabs.xltest.client

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import com.xebialabs.xltest.domain._
import com.xebialabs.xltest.json.XltJsonProtocol
import spray.client.pipelining._
import spray.http.{BasicHttpCredentials, _}
import spray.httpx.SprayJsonSupport._
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

object XltClient {

  /**
   * A wrapper for error messages extracted from non-successful responses.
   */
  class XltClientException(m: String) extends RuntimeException(m)

  /**
   *
   * Returns a failed [[Future]] for all the non-successful responses.
   */
  private[client] def failNonSuccessfulResponses(responseFuture: Future[HttpResponse]) = responseFuture.flatMap {
    case response if response.status.isFailure =>
      Future.failed(new XltClientException(response.entity.data.asString))
    case _ =>
      responseFuture
  }
}

class XltClient(apiUrl: String, username: String = "admin", password: String = "admin") extends XltJsonProtocol with AdditionalFormats with LazyLogging {

  implicit val system: ActorSystem = ActorSystem()
  implicit val timeout: Timeout = Timeout(30 days)
  val requestCounter = new AtomicInteger(0)

  val projectUri: Uri = s"$apiUrl/api/internal/projects"

  private val strictPipeline = (req: HttpRequest) => {
    val requestNum = requestCounter.getAndIncrement()
    val loggingReq = (i: HttpRequest) => {
      logger.debug(i.toString)
    }
    val loggingResp = (i: HttpResponse) => {
      logger.info(s"Request $requestNum execution done with ${i.status}")
      logger.debug(i.toString)
    }

    val pipeline = logRequest(loggingReq) ~>
      addCredentials(BasicHttpCredentials(username, password)) ~>
      sendReceive ~>
      logResponse(loggingResp)


    XltClient.failNonSuccessfulResponses(pipeline(req))
  }

  def createCis(cis: Seq[Ci]): Seq[Future[Ci]] = {
    cis.map(createCi)
  }

  def createCi(ci: Ci): Future[Ci] = {
    ci match {
      case p: Project => createProject(p)
    }
  }

  def createProject(p: Project): Future[Project] = strictPipeline(Post(s"$apiUrl/api/internal/projects", p)).map(_ ~> unmarshal[Project])

  def removeProject(id: String): Future[HttpResponse] = strictPipeline(Delete(s"$apiUrl/api/internal/projects/$id"))

  def findProjectByTitle(title: String): Future[Seq[Project]] = {
    strictPipeline(Get(projectUri withQuery ("title" -> title))).map(_ ~> unmarshal[Seq[Project]])
  }

  def getProjectByName(name: String): Future[Project] = {
    strictPipeline(Get(s"$projectUri/$name" )).map(_ ~> unmarshal[Project])
  }

  def createTestSpecification(specification: ExecutableTestSpecification, projectName: String): Future[HttpResponse] = strictPipeline(
    Post(s"$apiUrl/api/internal/projects/$projectName/testspecifications", specification))

  def createTestSpecification(specification: ActiveTestSpecification, projectName: String): Future[HttpResponse] = strictPipeline(
    Post(s"$apiUrl/api/internal/projects/$projectName/testspecifications", specification))

  def createTestSpecification(specification: PassiveTestSpecification, projectName: String): Future[HttpResponse] = strictPipeline(
    Post(s"$apiUrl/api/internal/projects/$projectName/testspecifications", specification))

  def findTestSpecifications(projectName: String): Future[Seq[BaseTestSpecification]] = strictPipeline(
    Get(s"$apiUrl/api/internal/projects/$projectName/testspecifications")).map(_ ~> unmarshal[Seq[PassiveTestSpecification]])

  def removeTestSpecification(id: String, projectName: String): Future[HttpResponse] = strictPipeline(
    Delete(s"$apiUrl/api/internal/projects/$projectName/testspecifications/$id"))

  def createDashboard(dashboard: Dashboard):Future[HttpResponse] = strictPipeline(
    Post(s"$apiUrl/api/internal/dashboards", dashboard)
  )

  def listDashboards(): Future[Seq[Dashboard]] = strictPipeline(Get(s"$apiUrl/api/internal/dashboards")).map(_ ~> unmarshal[Seq[Dashboard]])

  def listProjects(): Future[Seq[Project]] = strictPipeline(Get(s"$apiUrl/api/internal/projects")).map(_ ~> unmarshal[Seq[Project]])

  def createCompleteProject(cp: CompleteProject): Future[Seq[HttpResponse]] = {
    val f = createProject(cp.project)

    val tsFuture = f flatMap {
      case project: Project =>
        val id = project.name.get
        val tsCreationFutures: Seq[Future[HttpResponse]] = cp.testSpecifications.map {
          case ts: PassiveTestSpecification => createTestSpecification(ts, id)
          case ts: ActiveTestSpecification => createTestSpecification(ts, id)
          case ts: ExecutableTestSpecification => createTestSpecification(ts, id)
        }

        Future.sequence(tsCreationFutures)
    }
    tsFuture
  }

}
