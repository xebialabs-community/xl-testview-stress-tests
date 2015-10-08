package stress.chain

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.util.Random

object Testspecifications {

  val feeder = Iterator.continually(Map("testSpecTitle" -> Random.alphanumeric.take(10).mkString))

  def create =
    feed(feeder).exec(http("Create project")
      .post("/api/internal/projects")
      .body(StringBody("""{
            "id":"---",
            "type":"xlt.Project",
            "title":"project-${projectTitle}"
          }""")).asJSON)
}
