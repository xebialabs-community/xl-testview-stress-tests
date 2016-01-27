package stress.chain

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.xml.Elem

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

  def importTestData = {
    exec(http("Run import")
      .post("/api/internal/import/${testSpecId}")
      .header("Content-Type", "multipart/mixed")
      .bodyPart(StringBodyPart("metadata", metadata).contentType("application/json"))
      .bodyPart(RawFileBodyPart("test-results.zip", "${filename}").contentType("application/zip")))
  }


}
