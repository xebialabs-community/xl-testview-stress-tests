package com.xebialabs.xltest.json

import com.xebialabs.xltest.domain._
import spray.json._

trait XltJsonProtocol extends DefaultJsonProtocol with AdditionalFormats {
  this: ProductFormatsInstances =>

  implicit val projectFormat = jsonFormat4(Project.apply)
  implicit val passiveTestSpecificationFormat = jsonFormat9(PassiveTestSpecification.apply)
  implicit val activeTestSpecificationFormat = jsonFormat9(ActiveTestSpecification.apply)
  implicit val executableTestSpecificationFormat = jsonFormat12(ExecutableTestSpecification.apply)
  implicit val dashboardTileFormat = jsonFormat9(DashboardTile.apply)
  implicit val dashboardFormat = jsonFormat6(Dashboard.apply)

  implicit object CiProtocol extends RootJsonFormat[Ci] {
    def read(json: JsValue): Ci = {
      deserializationError("Read is not implemented")
    }

    def write(ci: Ci): JsValue = {
      ci match {
        case ci: Project => ci.toJson
        case ci: PassiveTestSpecification => ci.toJson
        case ci: ActiveTestSpecification => ci.toJson
        case ci: ExecutableTestSpecification => ci.toJson
        case ci: DashboardTile => ci.toJson
        case ci: Dashboard => ci.toJson
        case _ => serializationError(s"Undefined CI type ${ci.getClass}")
      }
    }
  }

}
