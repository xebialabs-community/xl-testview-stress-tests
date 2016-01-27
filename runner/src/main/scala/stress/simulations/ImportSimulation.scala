package stress.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.joda.time.DateTime
import stress._
import stress.chain.{TestSpecifications, JunitGradle, Import}
import stress.config.RunnerConfig
import stress.utils.ClientUtils

class ImportSimulation extends Simulation {

  val clientUtils = new ClientUtils
  val projectName = clientUtils.prepareProject(s"Import Simulation${DateTime.now()}")
  val testSpecFeeder = TestSpecifications.testSpecFeeder(projectName)

  val importTestResults = scenario("Import stuff").feed(testSpecFeeder)
    .repeat(RunnerConfig.importRuns.rounds) {
      feed(Import.testResultFeeder)
        .exec(JunitGradle.importTestData)
    }

  setUp(
    importTestResults.inject(
      nothingFor(RunnerConfig.importRuns.postWarmUpPause),
      rampUsers(RunnerConfig.importRuns.parallelTestSpecs) over RunnerConfig.importRuns.rampUpPeriod
      //      rampUsers(50) over RunnerConfig.simulations.rampUpPeriod
    )
  ).protocols(httpProtocol)

}


