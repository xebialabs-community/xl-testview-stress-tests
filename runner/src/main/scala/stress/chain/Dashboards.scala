package stress.chain

import com.xebialabs.xltest.domain.{DashboardTile, Dashboard}
import com.xebialabs.xltest.json.XltJsonProtocol
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import stress.config.RunnerConfig
import stress.utils.ClientUtils
import scala.util.Random
import spray.json._

import java.util.UUID

object Dashboards extends XltJsonProtocol {

  private val utils: ClientUtils = new ClientUtils

  lazy val dashboards = utils.listDashboards
  lazy val projects = utils.listProjects

  val dashboardFeeder = Iterator.continually(Map("dashboardName" -> Random.shuffle(dashboards).head.name.get))
  val projectFeeder = Iterator.continually(Map("projectName" -> Random.shuffle(projects).head.name.get))

  val sentHeaders = Map("Content-Type" -> "application/javascript", "Accept" -> "text/html")

  def browse =
    feed(dashboardFeeder).exec(http("Look at a dashboard")
      .get("/#/dashboards/${dashboardName}")
      .headers(sentHeaders))
      .pause(RunnerConfig.dashboards.minPause)

      .exec(http("Get dashboard CI")
        .get("/api/internal/dashboards/Configuration/Dashboards/${dashboardName}")
        .check(jsonPath("$.dashboardTiles[*]").findAll.saveAs("dashboardTiles")))

      .exec(session => session.set("reportsInDashboard", extractReports(session)))
      .foreach("${reportsInDashboard}", "report") {
        exec(http("Get report for dashboard")
          .get("${report}")
          .queryParam("maxResults", 10))
      }

  def addTile =
    feed(dashboardFeeder)
      .feed(projectFeeder)

      .exec(http("Get dashboard CI")
        .get("/api/internal/dashboards/Configuration/Dashboards/${dashboardName}")
        .check(substring("${dashboardName}"))
        .check(bodyString.saveAs("dashboardJSON")))
      .pause(RunnerConfig.dashboards.minPause, RunnerConfig.dashboards.maxPause)

      .exec(session => session.set("dashboardWithTile", addTileToDashboard(session)))

      .exec(http("Add a tile")
        .put("/api/internal/dashboards/Configuration/Dashboards/${dashboardName}")
        .body(StringBody("${dashboardWithTile}")))
      .pause(RunnerConfig.dashboards.minPause, RunnerConfig.dashboards.maxPause)


  def addTileToDashboard(session: Session): String = {
    val dashboardJSON = session("dashboardJSON").as[String]
    val projectName =  session("projectName").as[String]
    val testSpecId = utils.testSpecIdsByProjectName(projectName).head
    val dashboard = dashboardJSON.parseJson.convertTo[Dashboard]

    val newDashboard: Dashboard = dashboard.copy(dashboardTiles =
      List(DashboardTile(s"${dashboard.id}/${UUID.randomUUID().toString}", 0, 0, 5, 5, s"Tile${Random.nextInt}", testSpecId, "xlt.HealthBarometer")))

    newDashboard.toJson.toString
  }

  def extractReports(session: Session): Seq[String] = {
    val tiles: Seq[String] = session("dashboardTiles").as[Seq[String]]
    tiles
      .map(json => json.parseJson.convertTo[DashboardTile])
      .map(tile => s"/api/internal/reports/${tile.reportType}/testspecification/${tile.testSpecification.split("""/""").last}")
  }

}
