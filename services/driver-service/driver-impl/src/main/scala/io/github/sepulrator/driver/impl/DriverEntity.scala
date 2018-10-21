package io.github.sepulrator.driver.impl

import java.util.UUID

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger, PersistentEntity}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import io.github.sepulrator.driver.api.{Driver, DriverCreated, DriverEvent, DriverStatus}
import play.api.libs.json.{Format, Json}
import io.github.sepulrator.common.JsonFormats._

class DriverEntity extends PersistentEntity {
  override type Command = DriverCommand
  override type Event = PDriverEvent
  override type State = Option[PDriver]

  override def initialState: Option[PDriver] = None

  override def behavior: Behavior = {
    case None => notCreated
    case Some(driver) if driver.status == DriverStatus.Created => created(driver)
  }


  private val getDriverCommand = Actions().onReadOnlyCommand[GetDriver.type, Option[PDriver]] {
    case (GetDriver, ctx, state) => ctx.reply(state)
  }

  private val notCreated: Actions = {
    Actions().onCommand[CreateDriver, Done] {
      case (CreateDriver(driver), ctx, state) =>
        ctx.thenPersist(PDriverCreated(driver))(_ => ctx.reply(Done))
    }.onEvent {
      case (PDriverCreated(driver), state) => Some(driver)
    }.orElse(getDriverCommand)
  }

  private def created(driver: PDriver): Actions = {
    Actions().orElse(getDriverCommand)
  }

}

case class PDriver(
                 id: UUID,
                 fullName: String,
                 licenseNumber: String,
                 status: DriverStatus.Status
               ) {

  def create() = {
//    assert(status == DriverStatus.Created)
    copy(
      status = DriverStatus.Created
    )
  }
}

object PDriver {
  implicit val format: Format[PDriver] = Json.format
}

sealed trait DriverCommand

case object GetDriver extends DriverCommand with ReplyType[Option[PDriver]] {
  implicit val format: Format[GetDriver.type] = singletonFormat(GetDriver)
}

case class CreateDriver(driver: PDriver) extends DriverCommand with ReplyType[Done]

object CreateDriver {
  implicit val format: Format[CreateDriver] = Json.format
}


sealed trait PDriverEvent extends AggregateEvent[PDriverEvent] {
  override def aggregateTag: AggregateEventTagger[PDriverEvent] = PDriverEvent.ShardedTags
}

object PDriverEvent {
  val NumShards = 4
  val ShardedTags = AggregateEventTag.sharded[PDriverEvent]("PDriverEvent", NumShards)
}

case class PDriverCreated(driver: PDriver) extends PDriverEvent

object PDriverCreated {
  implicit val format: Format[PDriverCreated] = Json.format

}





