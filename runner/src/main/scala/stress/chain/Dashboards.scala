package stress.chain

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import stress.utils.ClientUtils

import scala.util.Random

object Dashboards {

  private val utils: ClientUtils = new ClientUtils
  val dashboards = utils.dashboards


  val dashboardFeeder = Iterator.continually(Map("dashboardName" -> Random.shuffle(dashboards).head))

  def browse =
    feed(dashboardFeeder).exec(http("Look at a dashboard")
      .get("#/dashboards/${dashboardName}")).pause(10)


}
