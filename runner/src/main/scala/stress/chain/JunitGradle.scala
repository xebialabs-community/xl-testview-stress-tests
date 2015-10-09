package stress.chain

import java.nio.file.{Files, Path}

import generator.Generator
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import stress.utils.{FileUtils, XmlOMatic}

import scala.collection.JavaConverters._
import scala.xml.{Elem, Node}

object JunitGradle {

  val basicTemplate: Elem = scala.xml.XML.load(getClass.getResource("/TEST-com.xebialabs.xltest.reference.JunitReference.xml"))
  val metadata =
    """{
    "source": "jenkins",
    "serverUrl": "http://my.jenkins.com:8080",
    "buildResult": "SUCCESS",
    "buildNumber": "123",
    "jobUrl": "http://my.jenkins.com:8080/job/test/",
    "jobName": "test",
    "buildUrl": "http://my.jenkins.com:8080/job/test/11/",
    "executedOn": "build-slave1",
    "buildParameters": {
      "buildParam1": "value1",
      "buildParam2": "value2"
    }
  }
    """

  val testSpecFeeder = Iterator.continually(Map("testSpecId" -> "40758f33-8aa0-4cc5-9cc4-ee37a73e2740"))

  val testResultFeeder = Iterator.continually(Map("filename" -> {
    val data: List[Path] = Generator.createTestData(10, 1, null).asScala.toList
    val updatedXml: List[List[Node]] = data.map(dir => {
      val xmlFiles: List[Path] = Files.newDirectoryStream(dir).asScala.toList
      val xmlNodes: List[Node] = xmlFiles.map(f => scala.xml.XML.load(Files.newInputStream(f)))
      xmlNodes.map(xmlNode => XmlOMatic.updateTime(xmlNode))
    })

    val listOfZips: List[Path] = updatedXml.map(f => FileUtils.zip(f.map(g => FileUtils.saveXml(g))))

    listOfZips.head.toAbsolutePath.toString
  }))

  def importTestData = {
    feed(testSpecFeeder).feed(testResultFeeder)
      .exec(http("Run import")
        .post("/api/internal/import/${testSpecId}")
        .header("Content-Type", "multipart/mixed")
        .bodyPart(StringBodyPart("metadata", metadata).contentType("application/json"))
        .bodyPart(RawFileBodyPart("test-results.zip", "${filename}").contentType("application/zip")))
  }

}
