package com.xebialabs.xlrelease.client

import com.xebialabs.xlrelease.client.XlrClient._
import com.xebialabs.xlrelease.domain._
import com.xebialabs.xlrelease.generator.SpecialDayGenerator
import com.xebialabs.xlrelease.json.{XltJsonProtocol, XlrJsonProtocol}
import com.xebialabs.xlrelease.support.UnitTestSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import spray.http.{HttpEntity, HttpResponse, StatusCodes}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@RunWith(classOf[JUnitRunner])
class XltClientTest extends UnitTestSugar with XltJsonProtocol {

  val client = new XltClient("http://localhost:6516")

  describe("XLT client") {
    it("should create a project") {
      val project = new Project("3", "MyProject")

      val createResponse = client.createProject(project).futureValue
      createResponse.status shouldBe StatusCodes.Created

      val removeResponse = client.removeProject(project.id).futureValue
      removeResponse.status shouldBe StatusCodes.NoContent
    }
//
//    it("should create a phase within release") {
//      val release = Release.build("ReleaseTest005")
//
//      val phase = Phase.build("Phase002", release.id)
//
//      val phaseResponse = for (
//        releaseResponse <- client.createCi(release);
//        phaseResponse <- client.createCi(phase)
//      ) yield phaseResponse
//
//      phaseResponse.futureValue.status shouldBe StatusCodes.OK
//
//      client.removeCi(release.id)
//    }
//
//    it("should create tasks") {
//      val release = Release.build("ReleaseTest103")
//      val phase = Phase.build("Phase002", release.id)
//
//      val taskResponse = for (
//        releaseResponse <- client.createCi(release);
//        phaseResponse <- client.createCi(phase);
//        taskResponse <- client.createCi(Task.build("Task002", phase.id))
//      ) yield taskResponse
//
//      taskResponse.futureValue.status shouldBe StatusCodes.OK
//
//      client.removeCi(release.id)
//    }
//
//    it("should create tasks and dependencies") {
//      val release = Release.build("ReleaseTest104")
//      val phase = Phase.build("Phase002", release.id)
//      val task = Task.build("Task002", phase.id).toGate
//      val dependency = Dependency.build("Dependency", task.id, task.id)
//
//      val dependencyResponse = for (
//        releaseResponse <- client.createCi(release);
//        phaseResponse <- client.createCi(phase);
//        taskResponse <- client.createCi(task);
//        dependencyResponse <- client.createCi(dependency)
//      ) yield dependencyResponse
//
//      dependencyResponse.futureValue.status shouldBe StatusCodes.OK
//
//      client.removeCi(release.id)
//    }
//
//    it("should create special days") {
//      val days = SpecialDayGenerator.generateSpecialDays()
//
//      expectSuccessfulResponse(client.createCis(days))
//
//      val removalsFuture = days.map(_.id).map(client.removeCi)
//      expectSuccessfulResponses(removalsFuture)
//    }
//
//    it("should create many releases in batches") {
//      val range = 0 until 20
//      val releases = range.map(id =>
//        Release.build(s"ReleaseTest$id")
//      )
//      val groups = releases.grouped(100).toSeq
//
//      val releaseResponsesFutures = groups.map {
//        case group: Seq[Release] => client.createCis(group.toSeq)
//      }
//      expectSuccessfulResponses(releaseResponsesFutures)
//
//      val releaseRemovalFutures = releases.map(release => {
//        client.removeCi(release.id)
//      })
//      expectSuccessfulResponses(releaseRemovalFutures)
//    }
//
//    it("should create many releases") {
//      val range = 0 until 20
//      val releases = range.map(id =>
//        Release.build(s"ReleaseTest$id")
//      )
//      val releaseResponsesFutures = releases.map(client.createCi)
//      expectSuccessfulResponses(releaseResponsesFutures)
//
//      val releaseRemovalFutures = releases.map(release => {
//        client.removeCi(release.id)
//      })
//      expectSuccessfulResponses(releaseRemovalFutures)
//    }
//
//    it("should import template from a file") {
//      val createResponseFuture = client.importTemplate("/many-automated-tasks.xlr")
//      expectSuccessfulResponse(createResponseFuture)
//    }
//
//    it("should fail future of non-successful responses") {
//
//      val response = new HttpResponse(StatusCodes.BadRequest, HttpEntity("Some bad thing has happened."))
//
//      val changedFuture = failNonSuccessfulResponses(Future.successful(response))
//
//      whenReady(changedFuture.failed) {
//        case ex => ex.getMessage shouldBe "Some bad thing has happened."
//      }
//    }
//
//    it("should not modify the future with successful response") {
//      val response = new HttpResponse(StatusCodes.OK, HttpEntity("Great success."))
//      val changedFuture = failNonSuccessfulResponses(Future.successful(response))
//      changedFuture.futureValue shouldBe response
//    }
//
//    it("should create server configurations") {
//      val directory = Directory("Configuration/Custom")
//      val server = HttpConnection(s"${directory.id}/ConfigurationJenkins", "Jenkins", "jenkins.Server")
//      val cis = Seq(directory, server)
//
//      expectSuccessfulResponse(client.createCis(cis))
//
//      val removalsFuture = cis.map(_.id).map(client.removeCi)
//      expectSuccessfulResponses(removalsFuture)
//    }
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
