package stress.chain

import io.gatling.core.Predef._
import stress.utils.ClientUtils

object TestSpecifications {

  def testSpecFeeder(projectName: String) = {
    val clientUtils = new ClientUtils
    val ids: Seq[String] = clientUtils.testSpecIds(projectName)
    val map: Array[Map[String, String]] = ids.map(id => Map("testSpecId" -> id))(collection.breakOut)
    map.queue
  }

}
