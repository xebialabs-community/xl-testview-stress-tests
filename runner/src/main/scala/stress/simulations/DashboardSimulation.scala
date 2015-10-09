package stress.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import stress._
import stress.chain.Dashboards
import stress.config.RunnerConfig
import scala.concurrent.duration._

class DashboardSimulation extends Simulation {

  val browseDashboards = scenario("Browse dashboards").exec(Dashboards.browse)

  setUp(
    browseDashboards.inject(
      rampUsers(RunnerConfig.simulations.users) over(10 seconds),
      nothingFor(RunnerConfig.simulations.postWarmUpPause),
      rampUsers(RunnerConfig.input.users) over RunnerConfig.simulations.rampUpPeriod

    )
  ).protocols(httpProtocol)
}
