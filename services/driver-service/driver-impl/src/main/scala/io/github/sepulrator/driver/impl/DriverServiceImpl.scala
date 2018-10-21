package io.github.sepulrator.driver.impl

import java.util.UUID

import akka.NotUsed
import com.datastax.driver.core.utils.UUIDs
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import io.github.sepulrator.driver.api.{Driver, DriverCreated, DriverEvent, DriverService}

import scala.concurrent.ExecutionContext

class DriverServiceImpl(registry: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends DriverService {

  override def createDriver: ServiceCall[Driver, Driver] = { req =>
    val driverId = UUIDs.timeBased()
    val pDriver = PDriver(driverId, req.fullName, req.licenseNumber, null)
    registry.refFor[DriverEntity](driverId.toString)
      .ask(CreateDriver(pDriver))
      .map(_ => convertDriver(pDriver))
  }

  override def getDriver(id: UUID): ServiceCall[NotUsed, Driver] = { req =>
    registry.refFor[DriverEntity](id.toString)
      .ask(GetDriver)
      .map {
        case Some(driver) => convertDriver(driver)
        case None => throw NotFound("Driver " + id + " not found")
      }
  }

  override def driverEvents: Topic[DriverEvent] = TopicProducer.taggedStreamWithOffset(PDriverEvent.ShardedTags.allTags.toList) { (tag, offset) =>
    registry.eventStream(tag, offset)
      .map( ev => (convertEvent(ev), ev.offset))
  }

  private def convertDriver(pDriver: PDriver): Driver = {
    Driver(Some(pDriver.id), pDriver.fullName, pDriver.licenseNumber, pDriver.status)
  }

  private def convertEvent(ev: EventStreamElement[PDriverEvent]): DriverEvent = {
    ev.event match {
      case PDriverCreated(driver) => DriverCreated(driver.id, driver.fullName, driver.licenseNumber, driver.status)
    }
  }
}
