package stress

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import stress.config.RunnerConfig

import scala.language.postfixOps

abstract class SimulationBase(scenarios: List[ScenarioBuilder]) extends Simulation {

  def this(scenario: ScenarioBuilder) = this(List(scenario))

  setUp(
    scenarios.map(
      _.inject(
        atOnceUsers(1),
        nothingFor(RunnerConfig.simulations.postWarmUpPause),
        rampUsers(RunnerConfig.input.users) over RunnerConfig.simulations.rampUpPeriod
      )
    )
  ).protocols(httpProtocol)

}
