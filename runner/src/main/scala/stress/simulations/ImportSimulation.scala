package stress.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import stress._
import stress.chain.{Testspecifications}
import stress.config.RunnerConfig

import scala.concurrent.duration._

class ImportSimulation extends Simulation {
  val importTestResults = scenario("Import stuff").exec(Testspecifications.create)

  setUp(
    importTestResults.inject(
      rampUsers(RunnerConfig.simulations.users) over(10 seconds),
      nothingFor(RunnerConfig.simulations.postWarmUpPause),
      rampUsers(RunnerConfig.input.users) over RunnerConfig.simulations.rampUpPeriod

    )
  ).protocols(httpProtocol)
}
