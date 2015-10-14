package stress.chain

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Reports {

  def getReport = exec(http("Retrieve report")
    .get("/api/internal/reports/${reportName}/testspecification/${testSpecificationName}")
    .queryParam("date", "${date}")
    .queryParam("range", "${range}")
  )


}
