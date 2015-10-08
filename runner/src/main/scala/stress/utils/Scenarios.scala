package stress.utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.StringBody
import stress.chain._
import stress.config.RunnerConfig._

import scala.language.{implicitConversions, postfixOps}

object Scenarios {

  val createProjectScenario = scenario("Create project")
    .exec(Projects.create)

  val importTestSpecData = scenario("Import data").exec(JunitGradle.importTestData)
}
