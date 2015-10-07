package com.xebialabs.xlrelease.client

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import com.typesafe.scalalogging.LazyLogging
import com.xebialabs.xlrelease.domain._
import com.xebialabs.xlrelease.json.{XltJsonProtocol}
import spray.client.pipelining._
import spray.client.pipelining._
import spray.http.{BasicHttpCredentials, _}
import spray.httpx.SprayJsonSupport._
import spray.httpx.unmarshalling._
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

  def createProject(p: Project): Future[HttpResponse] = strictPipeline(Post(s"$apiUrl/api/internal/projects", p))

  def removeProject(id: String): Future[HttpResponse] = strictPipeline(Delete(s"$apiUrl/api/internal/projects/$id"))

}
