package stress.simulations

import com.xebialabs.xltest.generator.DashboardGenerator
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.joda.time.DateTime
import stress._
import stress.chain.Dashboards
import stress.config.RunnerConfig
import stress.utils.ClientUtils
import scala.concurrent.duration._

class DashboardSimulation extends Simulation {


  val client = ClientUtils.getDefaultClient

  DashboardGenerator.createEmptyDashboardPayload(10, s"Dashboard Simulation${DateTime.now()}").foreach(client.createDashboard)

  val browseDashboards = scenario("Browse dashboards").exec(Dashboards.browse)

  setUp(
    browseDashboards.inject(
      nothingFor(RunnerConfig.dashboards.postWarmUpPause),
      rampUsers(RunnerConfig.dashboards.users) over RunnerConfig.dashboards.rampUpPeriod

    )
  ).protocols(httpProtocol)
}
