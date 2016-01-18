package stress.chain

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.Body
import org.joda.time.DateTime

import scala.util.Random

object Projects {

  val feeder = Iterator.continually(Map("projectTitle" -> Random.alphanumeric.take(10).mkString))

  def create =
    feed(feeder).exec(http("Create project")
      .post("/api/internal/projects")
      .body(StringBody("""{
            "id":"",
            "type":"xlt.Project",
            "title":"project-${projectTitle}"
          }""")).asJSON)
}
