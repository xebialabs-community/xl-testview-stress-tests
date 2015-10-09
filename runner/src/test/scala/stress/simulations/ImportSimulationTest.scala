package stress.simulations

import com.xebialabs.xltest.support.UnitTestSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ImportSimulationTest extends UnitTestSugar {

  describe("XLT client") {
    it("Should generate files") {
      val simulation = new ImportSimulation
      val next: Map[String, String] = simulation.testResultFeeder.next()

      println("--")
    }
  }
}
