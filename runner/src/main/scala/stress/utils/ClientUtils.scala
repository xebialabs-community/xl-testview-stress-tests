package stress.utils

import com.xebialabs.xltest.client.{XltClient}
import com.xebialabs.xltest.domain.Dashboard
import com.xebialabs.xltest.json.XltJsonProtocol
import spray.http.HttpResponse
import spray.httpx.unmarshalling._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

class ClientUtils(url: String = "http://localhost:6516") extends XltJsonProtocol{

  def client = new XltClient(url)

  private val dashboardsF: Future[Seq[Dashboard]] = client.listDashboards()

  val dashboards: Seq[Dashboard] = Await.result(dashboardsF, 10 seconds)

}
