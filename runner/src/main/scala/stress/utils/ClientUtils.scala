package stress.utils

import com.xebialabs.xltest.client.XltClient
import com.xebialabs.xltest.domain.Dashboard
import spray.http.HttpResponse
import spray.httpx.unmarshalling._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class ClientUtils(url: String = "http://localhost:6516") {
  private val timeout: Duration = Duration.create("10 seconds")

  def client = new XltClient(url)

  val dashboardsF: Future[Seq[String]] = client.listDashboards() map {
    case hp: HttpResponse =>
      val dashboards: Seq[Dashboard] = hp.entity.as[Seq[Dashboard]].right.get
      dashboards map (d => d.name.get)
  }


  val dashboards: Seq[String] = dashboardsF.result(timeout)
}
