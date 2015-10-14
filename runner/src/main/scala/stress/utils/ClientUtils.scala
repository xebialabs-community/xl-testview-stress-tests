package stress.utils

import com.xebialabs.xltest.client.XltClient
import com.xebialabs.xltest.domain.{BaseTestSpecification, CompleteProject, Dashboard, Project}
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

  private val dashboardsF: Future[Seq[Dashboard]] = client.listDashboards()

  val dashboards: Seq[Dashboard] = Await.result(dashboardsF, 10 seconds)

  def testSpecIds(projectName: String): Seq[String] = {
    val projectF: Future[Seq[Project]] = client.findProject(projectName)
    val project = Await.result(projectF, 10 seconds).head

    val specifications: Seq[BaseTestSpecification] = Await.result(client.findTestSpecifications(project.name.get), 10 seconds)

    specifications.map(sp => sp.name.get)
  }

  def prepareProject(): String = {
    val projectName = s"Project-${DateTime.now()}"
    val set: CompleteProject = generator.generateLargeJunitTestSet(projectName, 50)
    Await.result(client.createCompleteProject(set), 10 seconds)
    projectName
  }
}

object ClientUtils {
  val clientUtils = new ClientUtils
  def getDefaultClient = clientUtils.client
}