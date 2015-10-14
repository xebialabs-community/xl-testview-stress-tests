package stress.config

import org.scalatest._
class RunnerConfigTest extends FunSpecLike with Matchers {

  describe("RunnerConfig"){
    it("should define all properties") {
      RunnerConfig.password should not be ""
      RunnerConfig.username should not be ""
      RunnerConfig.simulation should not be ""
      RunnerConfig.dashboards.postWarmUpPause should not be ""
      RunnerConfig.dashboards.rampUpPeriod should not be ""
      RunnerConfig.dashboards.users should not be ""
      RunnerConfig.importRuns.filesPerTestSpec should not be 0
      RunnerConfig.importRuns.parallelTestSpecs should not be ""
      RunnerConfig.importRuns.postWarmUpPause should not be ""
      RunnerConfig.importRuns.rampUpPeriod should not be ""
      RunnerConfig.importRuns.rounds should not be ""
      RunnerConfig.projects.rampUpPeriod should not be ""
      RunnerConfig.projects.users should not be ""
    }
  }
}
