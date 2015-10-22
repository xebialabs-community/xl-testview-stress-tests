package stress.utils

import com.typesafe.scalalogging.LazyLogging
import io.gatling.app.Gatling
import io.gatling.core.scenario.Simulation
import stress.config.RunnerConfig
import stress.simulations.{ProjectSimulation, ImportSimulation, DashboardSimulation}

import scala.util.{Failure, Success, Try}

/**
 * This runner allows to find and execute simulations from the same classpath as the runner itself.
 * Simplifies build logic.
 */
object GatlingRunner extends App with LazyLogging {

  logger.info("Starting XL stress tests suite.")

  private val simulationProvValue = RunnerConfig.simulation

  println(simulationProvValue)

  private val simulationsToRun = simulationProvValue
    .split(",")
    .map(simulationClassByName)

  private def simulationClassByName(className: String): Class[_] = {
    Try(Class.forName(className)) match {
      case Success(c) => c
      case Failure(e) =>
        logger.error(s"Can not find simulation $className.")
        throw e
    }
  }

  logger.info(s"Running following simulations: ${simulationsToRun.map(_.getCanonicalName).mkString(", ")}")

  simulationsToRun.foreach {
    case simulation =>
      logger.info(s"Starting simulation $simulation")
      Gatling.fromArgs(args, Some(simulation.asInstanceOf[Class[Simulation]])) match {
        case 0 =>
          logger.info(s"Finished execution of $simulation.")
        case e =>
          logger.info(s"Gatling returned non-zero exit code $e. Exiting.")
          sys.exit(e)
      }
  }

  sys.exit(0)
}
