package stress.simulations

import com.xebialabs.xltest.generator.DashboardGenerator
import io.gatling.core.Predef._
import stress._
import stress.chain.{Projects, Dashboards}
import stress.config.RunnerConfig
import stress.utils.ClientUtils

class UserSimulation extends Simulation {


  val client = ClientUtils.getDefaultClient

  DashboardGenerator.createEmptyDashboardPayload(RunnerConfig.userMix.users).foreach(client.createDashboard)

  val users = scenario("Typical User")
    .repeat(RunnerConfig.userMix.rounds) {
      // TODO need to create projects with test data, currently relying on projects from ImportSimulation
        exec(Dashboards.browse)
    }

  setUp(
    users.inject(
      rampUsers(RunnerConfig.userMix.users) over RunnerConfig.userMix.rampUpPeriod
    )
  ).protocols(httpProtocol)

}
