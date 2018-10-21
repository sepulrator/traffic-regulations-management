package io.github.sepulrator.driver.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, ReadSideProcessor}
import com.lightbend.lagom.scaladsl.persistence.slick.SlickReadSide
import io.github.sepulrator.driver.api.DriverStatus

class DriverEventsReadSideProcessor(readSide: SlickReadSide, driverRepository: DriverRepository)
  extends ReadSideProcessor[PDriverEvent] {

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[PDriverEvent] =
    readSide
      .builder[PDriverEvent]("driver-readside")
    .setGlobalPrepare(driverRepository.createTable)
    .setEventHandler[PDriverCreated] { evt =>
      val driver = evt.event.driver
      driverRepository.save(DriverSnapshot(driver.id, driver.fullName, driver.licenseNumber, driver.status.toString))

    }
    .build()

  override def aggregateTags = PDriverEvent.ShardedTags.allTags
}
