package io.github.sepulrator.driver.api

import java.util.UUID

import play.api.libs.json.{Format, Json}

sealed trait DriverEvent {
  val driverId: UUID
}

case class DriverCreated(
  driverId: UUID,
  fullName: String,
  licenseNumber: String,
  status: DriverStatus.Status
) extends DriverEvent

object DriverCreated {
  implicit val format: Format[DriverCreated] = Json.format
}
