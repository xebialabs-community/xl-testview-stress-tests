import io.gatling.core.Predef._
import io.gatling.http.Predef._
import stress.config.RunnerConfig

import scala.language.{implicitConversions, postfixOps}

package object stress {

  val httpProtocol = http
      .baseURLs(RunnerConfig.baseUrls)
      .acceptHeader("application/json")
      .basicAuth(RunnerConfig.username, RunnerConfig.password)
      .contentTypeHeader("application/json; charset=UTF-8").build
}
