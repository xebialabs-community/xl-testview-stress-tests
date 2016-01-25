package stress.chain

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import stress.config.RunnerConfig
import stress.utils.ClientUtils

import scala.util.Random

object Dashboards {

  private val utils: ClientUtils = new ClientUtils
  lazy val dashboards = utils.dashboards

  val dashboardFeeder = Iterator.continually(Map("dashboardName" -> Random.shuffle(dashboards).head.name.get))

  val sentHeaders = Map("Content-Type" -> "application/javascript", "Accept" -> "text/html")

  def browse =
    feed(dashboardFeeder).exec(http("Look at a dashboard")
      .get("/#/dashboards/${dashboardName}")
    .headers(sentHeaders))
      .pause(RunnerConfig.dashboards.minPause, RunnerConfig.dashboards.maxPause)

}
