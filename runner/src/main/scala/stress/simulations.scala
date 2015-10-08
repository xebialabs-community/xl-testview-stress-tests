package stress

import io.gatling.core.Predef._
import stress.config.RunnerConfig
import stress.utils.Scenarios._

import scala.language.{implicitConversions, postfixOps}



class ProjectSimulation extends SimulationBase(createProjectScenario)
class ImportSimulation extends SimulationBase(importTestSpecData)