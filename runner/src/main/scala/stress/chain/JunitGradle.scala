package stress.chain

import java.nio.file.{Path, Paths}

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import stress.utils.{XmlOMatic, FileUtils}

import scala.util.Random
import scala.xml.transform.{RuleTransformer, RewriteRule}
import scala.xml.{Node, Elem}

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

  val testSpecFeeder = Iterator.continually(Map("testSpecId" -> "95331eb4-5536-4e75-a11c-57adb0ae2541"))

  val testResultFeeder = Iterator.continually(Map("filename" -> {
    val xml = XmlOMatic.updateTime(basicTemplate)
    val path: Path = FileUtils.saveXml(xml)
    val zip: Path = FileUtils.zip(List(path))

    zip.toAbsolutePath.toString
  }))

  def importTestData = {
    feed(testSpecFeeder).feed(testResultFeeder)
      .exec(http("Run import")
        .post("/api/internal/import/${testSpecId}").header("Content-Type", "multipart/mixed")
        .bodyPart(StringBodyPart("metadata", metadata).contentType("application/json"))
        .bodyPart(RawFileBodyPart("test-results.zip", "${filename}").contentType("application/zip")))
  }

}
