package com.xebialabs.xltest.client

import com.xebialabs.xltest.domain._
import com.xebialabs.xltest.generator.TestSpecificationGenerator
import com.xebialabs.xltest.json.XltJsonProtocol
import com.xebialabs.xltest.support.UnitTestSugar
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterEach, BeforeAndAfter}
import org.scalatest.junit.JUnitRunner
import spray.http.{HttpResponse, StatusCodes}
import spray.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class XltClientTest extends UnitTestSugar with XltJsonProtocol with BeforeAndAfterEach {
  val generator = new TestSpecificationGenerator

  val client = new XltClient("http://localhost:6516")

  override def beforeEach() {
    deleteProject("MyProject")
    deleteProject("Project A")
  }


  def deleteProject(project: String): Unit = {
    val entity = client.findProject(project).futureValue.entity

    val toOption = entity.toOption

    toOption.foreach(o => {
      val projects: Seq[Project] = o.asString.parseJson.convertTo[Seq[Project]]
      projects.foreach(p => {
        p.name match {
          case o: Some[String] => client.removeProject(o.get)
          case None =>
        }
      })

    })
  }

  describe("XLT client") {
    it("should create a project") {
      val project = new Project("3", "MyProject")

      val createResponse = client.createProject(project).futureValue
      createResponse.status shouldBe StatusCodes.Created

      val removeResponse = client.removeProject(project.id).futureValue
      removeResponse.status shouldBe StatusCodes.NoContent
    }

    it("should create a test specification") {
      val project = new Project("3", "MyProject")

      val createResponse = client.createProject(project).futureValue
      createResponse.status shouldBe StatusCodes.Created

      val entity = createResponse.entity
      val project1 = entity.asString.parseJson.convertTo[Project]

      val specification: PassiveTestSpecification = new PassiveTestSpecification("---", "Test Spec A", "xlt.DefaultFunctionalTestsQualifier", "xlt.JUnit")
      val createdResponse2 = client.createTestSpecification(
        specification, project1.name.get).futureValue
      createdResponse2.status shouldBe StatusCodes.Created

      val removeResponse = client.removeProject(project.id).futureValue
      removeResponse.status shouldBe StatusCodes.NoContent
    }

    it("should create a Simple Project") {

      val set: CompleteProject = generator.generateSimpleTestSet
      val createResponse = client.createCompleteProject(set).futureValue

      createResponse.seq.head.status shouldBe StatusCodes.Created
    }
  }

  def expectSuccessfulResponses(responsesFutures: Seq[Future[HttpResponse]]): Unit = {
    val releaseResponses = Future.sequence(responsesFutures).futureValue
    releaseResponses.foreach(releaseResponse =>
      releaseResponse.status.intValue should (be >= 200 and be < 300)
    )
  }

  def expectSuccessfulResponse(responseFuture: Future[HttpResponse]): Unit = {
    val response = responseFuture.futureValue
    response.status.intValue should (be >= 200 and be < 300)
  }

}