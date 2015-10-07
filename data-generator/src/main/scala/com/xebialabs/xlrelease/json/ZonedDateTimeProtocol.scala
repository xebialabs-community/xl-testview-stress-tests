package com.xebialabs.xlrelease.json

import org.threeten.bp.{ZoneId, ZonedDateTime}
import org.threeten.bp.format.DateTimeFormatter
import spray.json._

private [json] trait ZonedDateTimeProtocol extends DefaultJsonProtocol {
  implicit object ZonedDateTimeProtocol extends RootJsonFormat[ZonedDateTime] {

    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault)

    def write(obj: ZonedDateTime): JsValue = {
      JsString(formatter.format(obj))
    }

    def read(json: JsValue): ZonedDateTime = json match {
      case JsString(s) => try {
        ZonedDateTime.parse(s, formatter)
      } catch {
        case t: Throwable => error(s)
      }
      case _ =>
        error(json.toString())
    }

    def error(v: Any): ZonedDateTime = {
      val example = formatter.format(ZonedDateTime.now())
      deserializationError(f"'$v' is not a valid date value. Dates must be in compact ISO-8601 format, e.g. '$example'")
    }
  }
}