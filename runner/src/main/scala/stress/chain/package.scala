package stress

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

package object chain {

  implicit class PipedChainBuilder(chainBuilder: ChainBuilder) {

    def execGetDependencies(scenarioName: String): ChainBuilder = chainBuilder
      .exitHereIfFailed
      .exec(session => {
        val ids = session("releaseIds").as[Vector[String]]
        session.set("dependenciesBody", s"""{"ids":[${ids.map(s => s""""$s"""").mkString(",")}]}""")
      })
      .exec(
        http(stringToExpression(s"Get $scenarioName release dependencies"))
          .post("/dependencies")
          .body(StringBody("${dependenciesBody}"))
          .asJSON
      )
  }


}
