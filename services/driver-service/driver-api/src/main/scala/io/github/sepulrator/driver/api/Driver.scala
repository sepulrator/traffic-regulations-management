package io.github.sepulrator.driver.api

import java.util.UUID

import com.lightbend.lagom.scaladsl.api.deser.PathParamSerializer
import play.api.libs.json.{Format, Json}
import io.github.sepulrator.common.JsonFormats._


case class Driver (
  id: Option[UUID],
  fullName: String,
  licenseNumber: String,
  status: DriverStatus.Status
)

object Driver {
  implicit val format: Format[Driver] = Json.format

  def create(fullName: String, licenseNumber: String): Driver = {
    Driver(None, fullName, licenseNumber, DriverStatus.Created)
  }
}


object DriverStatus extends Enumeration {
  val Created, Active, Suspended, Cancelled = Value
  type Status = Value

  implicit val format: Format[Value] = enumFormat(this)
  implicit val pathParamSerializer: PathParamSerializer[Status] =
    PathParamSerializer.required("driverStatus")(withName)(_.toString)
}
