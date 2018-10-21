package io.github.sepulrator.driver.impl
import akka.stream.Materializer
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.slick.SlickPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.rp.servicediscovery.lagom.scaladsl.LagomServiceLocatorComponents
import io.github.sepulrator.driver.api.{Driver, DriverService}
import play.api.db.HikariCPComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.softwaremill.macwire._

abstract class DriverApplication(context: LagomApplicationContext) extends LagomApplication(context)
  with SlickPersistenceComponents
  with HikariCPComponents
  with LagomKafkaComponents
  with AhcWSComponents
{

  override def lagomServer: LagomServer = serverFor[DriverService](wire[DriverServiceImpl])
  override def jsonSerializerRegistry: JsonSerializerRegistry = DriverSerializerRegistry
  val driverService = serviceClient.implement[DriverService]

  persistentEntityRegistry.register(wire[DriverEntity])
  readSide.register[PDriverEvent](wire[DriverEventsReadSideProcessor])

  lazy val accountRepository = wire[DriverRepository]


}

class DriverApplicationLoader extends LagomApplicationLoader {
  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new DriverApplication(context) with LagomDevModeComponents

  override def load(context: LagomApplicationContext): LagomApplication =
    new DriverApplication(context) with LagomServiceLocatorComponents

  override def describeService = Some(readDescriptor[DriverService])
}


object DriverSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
    JsonSerializer[Driver],

    JsonSerializer[CreateDriver],
    JsonSerializer[GetDriver.type],

    JsonSerializer[PDriverCreated]

  )
}