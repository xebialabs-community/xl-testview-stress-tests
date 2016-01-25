package stress.simulations

import io.gatling.core.Predef._
import stress._
import stress.chain.{Projects, Dashboards}
import stress.config.RunnerConfig

class UserSimulation extends Simulation {

  val users = scenario("Typical User")
    .repeat(RunnerConfig.userMix.rounds) {
        exec(Dashboards.browse, Projects.create)
    }

  setUp(
    users.inject(
      rampUsers(RunnerConfig.userMix.users) over RunnerConfig.userMix.rampUpPeriod
    )
  ).protocols(httpProtocol)

}
