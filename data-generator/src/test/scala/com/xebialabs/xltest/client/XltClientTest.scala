package com.xebialabs.xltest.client

import com.xebialabs.xltest.domain._
import com.xebialabs.xltest.generator.TestSpecificationGenerator
import com.xebialabs.xltest.json.XltJsonProtocol
import com.xebialabs.xltest.support.UnitTestSugar
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterEach
import org.scalatest.junit.JUnitRunner
import spray.http.{HttpResponse, StatusCodes}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@RunWith(classOf[JUnitRunner])
class XltClientTest extends UnitTestSugar with XltJsonProtocol with BeforeAndAfterEach {
  val generator = new TestSpecificationGenerator

  val client = new XltClient("http://localhost:6516")

  override def beforeEach() {
    deleteProject("MyProject")
    deleteProject("Project A")
  }


  def deleteProject(projectName: String): Unit = {
    try {
      val projects = client.findProject(projectName).futureValue

      projects.foreach { p =>
        client.removeProject(p.name.get)
      }
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  describe("XLT client") {
    //    it("should find a project") {
    //      val projects: Future[Seq[Project]] = client.findProject("MyProject")
    //      projects.futureValue.head.title shouldBe "MyProject"
    //    }

    it("should create a project") {
      val project = new Project("3", "MyProject")

      val createdProject = client.createProject(project).futureValue
      createdProject.name shouldBe defined

      val removeResponse = client.removeProject(project.id).futureValue
      removeResponse.status shouldBe StatusCodes.NoContent
    }

    it("should create a test specification") {
      val project = new Project("3", "MyProject")

      val createdProject = client.createProject(project).futureValue
      createdProject.name shouldBe defined
      val projectName = createdProject.name.get

      val specification: PassiveTestSpecification = new PassiveTestSpecification( "Test Spec A", Some("xlt.DefaultFunctionalTestsQualifier"), "xlt.JUnit")
      val createdResponse2 = client.createTestSpecification(specification, projectName).futureValue
      createdResponse2.status shouldBe StatusCodes.Created

      val specifications: Seq[BaseTestSpecification] = client.findTestSpecifications(projectName).futureValue

      specifications.size shouldBe 1

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