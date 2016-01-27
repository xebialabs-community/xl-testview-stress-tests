package stress.utils

import com.xebialabs.xltest.client.XltClient
import com.xebialabs.xltest.domain._
import com.xebialabs.xltest.generator.TestSpecificationGenerator
import com.xebialabs.xltest.json.XltJsonProtocol
import org.joda.time.DateTime
import stress.config.RunnerConfig

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.Random

class ClientUtils extends XltJsonProtocol {
  def random = new Random()

  def client = new XltClient(RunnerConfig.baseUrls.head)

  def generator = new TestSpecificationGenerator()

  def testSpecIds(projectTitle: String): Seq[String] = {
    val projectF: Future[Seq[Project]] = client.findProjectByTitle(projectTitle)
    val project = Await.result(projectF, 10 seconds).head

    val specifications: Seq[BaseTestSpecification] = Await.result(client.findTestSpecifications(project.name.get), 10 seconds)

    specifications.map(sp => sp.name.get)
  }

  def testSpecIdsByProjectName(projectName: String): Seq[String] = {
    val projectF: Future[Project] = client.getProjectByName(projectName)
    val project = Await.result(projectF, 10 seconds)

    val specifications: Seq[BaseTestSpecification] = Await.result(client.findTestSpecifications(project.name.get), 10 seconds)

    specifications.map(sp => sp.id)
  }

  def prepareProject(projectName: String = s"Project-${DateTime.now()}"): String = {
    val set: CompleteProject = generator.generateLargeJunitTestSet(projectName, 50)
    Await.result(client.createCompleteProject(set), 10 seconds)
    projectName
  }

  def listDashboards(): Seq[Dashboard] = {
    val dashboardsF: Future[Seq[Dashboard]] = client.listDashboards()
    Await.result(dashboardsF, 10 seconds)
  }

  def listProjects(): Seq[Project] = {
    val projectsF: Future[Seq[Project]] = client.listProjects()
    Await.result(projectsF, 10 seconds)
  }
}

object ClientUtils {
  val clientUtils = new ClientUtils
  def getDefaultClient = clientUtils.client
}