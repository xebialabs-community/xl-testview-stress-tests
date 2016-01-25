package stress.config

import java.util.concurrent.TimeUnit.MILLISECONDS

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.{Duration, FiniteDuration}

/**
 * When adding a new configuration option don't forget to check that it has the same path at `runner.conf`.
 * If you add a duration, use [[RunnerConfig.duration()]] for accessing it.
 */
object RunnerConfig extends LazyLogging {


  lazy private val rootConfig = ConfigFactory.load("runner.conf").getConfig("xl")


  private val durationDilation = rootConfig.getDouble("duration-dilation")


  /*
  The common server options
   */
  val simulation = rootConfig.getString("simulation")

  def baseUrls: List[String] = rootConfig.getString("baseUrl").split(",").toList

  val username = rootConfig.getString("username")

  val password: String = rootConfig.getString("password")

  /**
   * The simulation specific options
   */
  object projects {
    private val sim = rootConfig.getConfig("sim.projects")
    val users = sim.getInt("users")
    val rampUpPeriod = duration(sim, "ramp-up-period")
  }

  object dashboards {
    private val sim = rootConfig.getConfig("sim.dashboards")
    val users = sim.getInt("users")
    val minPause = sim.getInt("min-pause")
    val maxPause = sim.getInt("max-pause")
    val rampUpPeriod = duration(sim, "ramp-up-period")
    val postWarmUpPause = duration(sim, "post-warm-up-pause")
  }

  object importRuns {
    private val sim = rootConfig.getConfig("sim.import-runs")
    val parallelTestSpecs = sim.getInt("parallel-test-specs")
    val filesPerTestSpec = sim.getInt("files-per-test-spec")
    val rounds = sim.getInt("rounds")
    val rampUpPeriod = duration(sim, "ramp-up-period")
    val postWarmUpPause = duration(sim, "post-warm-up-pause")
  }

  object userMix {
    private val sim = rootConfig.getConfig("sim.user-mix")
    val users = sim.getInt("users")
    val rounds = sim.getInt("rounds")
    val rampUpPeriod = duration(sim, "ramp-up-period")
    val postWarmUpPause = duration(sim, "post-warm-up-pause")
  }

  // Helpers

  /**
   * Always use this method to calculate duration. It will also take into account [[durationDilation]].
   */
  private def duration(parent: Config, path: String): FiniteDuration = {

    val duration = Duration(parent.getDuration(path, MILLISECONDS), MILLISECONDS)

    duration * durationDilation match {
      case fd: FiniteDuration => fd
      case _: Duration =>
        logger.warn(s"Using dilation factor $durationDilation resulted in infinite duration for $path. Falling back to non-dilated value: $duration.")
        duration
    }
  }
}
