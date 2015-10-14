package stress.simulations

import io.gatling.core.Predef._
import stress._
import stress.chain.Projects
import stress.config.RunnerConfig

class ProjectSimulation extends Simulation {

  val createProjects = scenario("Create projects").exec(Projects.create)

  setUp(
    createProjects.inject(
      nothingFor(RunnerConfig.dashboards.postWarmUpPause),
      rampUsers(RunnerConfig.dashboards.users) over RunnerConfig.dashboards.rampUpPeriod
    )
  ).protocols(httpProtocol)
}
