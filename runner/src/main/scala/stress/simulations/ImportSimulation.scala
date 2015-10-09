package stress.simulations

import java.nio.file.{Files, Path}

import generator.Generator
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import stress._
import stress.chain.JunitGradle
import stress.config.RunnerConfig
import stress.utils.{FileUtils, XmlOMatic}

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.xml.Node

class ImportSimulation extends Simulation {
  val generator = new Generator()


  val testResultFeeder = Iterator.continually(Map("filename" -> {
    val data: List[Path] = generator.createTestData(10, 1, null).asScala.toList
    val updatedXml: List[List[Node]] = data.map(dir => {
      val xmlFiles: List[Path] = scanDirectories(dir)
      val xmlNodes: List[Node] = loadXmlFiles(xmlFiles)
      updateTimestamps(xmlNodes)
    })

    val listOfZips: List[Path] = updatedXml.map(f => FileUtils.zip(f.map(g => FileUtils.saveXml(g))))

    listOfZips.head.toAbsolutePath.toString
  }))

  def updateTimestamps(xmlNodes: List[Node]): List[Node] = {
    xmlNodes.map(xmlNode => XmlOMatic.updateTime(xmlNode))
  }

  def loadXmlFiles(xmlFiles: List[Path]): List[Node] = {
    val xmlNodes: List[Node] = xmlFiles.map(f => scala.xml.XML.load(Files.newInputStream(f)))
    xmlNodes
  }

  def scanDirectories(dir: Path): List[Path] = {
    val xmlFiles: List[Path] = Files.newDirectoryStream(dir).asScala.toList
    xmlFiles
  }

  val importTestResults = scenario("Import stuff").feed(testResultFeeder).exec(JunitGradle.importTestData)

  setUp(
    importTestResults.inject(
      rampUsers(RunnerConfig.simulations.users) over (10 seconds),
      nothingFor(RunnerConfig.simulations.postWarmUpPause),
      rampUsers(RunnerConfig.input.users) over RunnerConfig.simulations.rampUpPeriod

    )
  ).protocols(httpProtocol)
}
