package io.github.sepulrator.driver.api

import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.Service.restCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait DriverService extends Service {

  def createDriver: ServiceCall[Driver, Driver]

  def getDriver(id: UUID): ServiceCall[NotUsed, Driver]

  def driverEvents: Topic[DriverEvent]

  final override def descriptor: Descriptor = {
    import Service._

    named("driver").withCalls(
      restCall(Method.POST, "/api/drivers", createDriver),
      restCall(Method.GET, "/api/drivers/:id", getDriver _)
    ).withAutoAcl(true)
  }

}
