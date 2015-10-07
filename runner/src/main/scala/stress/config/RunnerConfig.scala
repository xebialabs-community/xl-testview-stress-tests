package stress.config

import java.util.concurrent.TimeUnit.MILLISECONDS

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.{FiniteDuration, Duration}

/**
 * When adding a new configuration option don't forget to check that it has the same path at `runner.conf`.
 * If you add a duration, use [[RunnerConfig.duration()]] for accessing it.
 */
object RunnerConfig extends LazyLogging {

  private val CONFIG_OBJECT_PATH = "xl.runner"

  lazy private val rootConfig = ConfigFactory.load("runner.conf")

  lazy private val runnerConfig = rootConfig.getConfig(CONFIG_OBJECT_PATH)

  private val durationDilation = runnerConfig.getDouble("durationDilation")

  /**
   * This object contains public, user-facing config parameters.
   */
  object input {

    val users = rootConfig.getInt("xl.runner.input.users")

    def baseUrls: List[String] = runnerConfig.getString("input.baseUrl").split(",").toList

    val username = runnerConfig.getString("input.username")

    val password = runnerConfig.getString("input.password")

    val teams = runnerConfig.getInt("input.teams")

    val ops = runnerConfig.getInt("input.ops")

    val releaseManagers = runnerConfig.getInt("input.releaseManagers")

    val sshHost = runnerConfig.getString("input.sshHost")

    val sshUser = runnerConfig.getString("input.sshUser")

    val sshPassword = runnerConfig.getString("input.sshPassword")
  }

  val releaseManagerPauseMin = duration("releaseManagerPauseMin")

  val releaseManagerPauseMax = duration("releaseManagerPauseMax")

  val opsPauseMin = duration("opsPauseMin")

  val opsPauseMax = duration("opsPauseMax")

  val devPause = duration("devPause")

  val taskPollPause = duration("taskPollPause")

  val taskPollDuration = duration("taskPollDuration")

  object queries {
    object search {
      val numberByPage = runnerConfig.getInt("queries.search.numberByPage")
    }
  }

  object simulations {

    val postWarmUpPause =  duration("simulations.postWarmUpPause")

    val rampUpPeriod = duration("simulations.rampUpPeriod")

    object realistic {

      val rampUpPeriod = duration("simulations.realistic.rampUpPeriod")

      val repeats = runnerConfig.getInt("simulations.realistic.repeats")

    }
  }


  // Helpers

  /**
   * Always use this method to calculate duration. It will also take into account [[durationDilation]].
   */
  private def duration(path: String): FiniteDuration = {

    val duration = Duration(runnerConfig.getDuration(path, MILLISECONDS), MILLISECONDS)

    duration * durationDilation match {
      case fd: FiniteDuration => fd
      case _: Duration =>
        logger.warn(s"Using dilation factor $durationDilation resulted in infinite duration for $path. Falling back to non-dilated value: $duration.")
        duration
    }
  }
}
