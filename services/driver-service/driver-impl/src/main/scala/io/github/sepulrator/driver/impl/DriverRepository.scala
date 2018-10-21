package io.github.sepulrator.driver.impl

import java.util.UUID

import akka.Done
import akka.stream.Materializer
import io.github.sepulrator.driver.api.DriverStatus
import slick.lifted.{TableQuery, Tag}
import slick.jdbc.MySQLProfile.api._
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

case class DriverSnapshot(id: UUID, fullName: String, licenseNumber: String, status: String)

class DriverRepository {

  class DriverTable(tag: Tag) extends Table[DriverSnapshot](tag, "driver") {

    def id = column[UUID]("id", O.PrimaryKey)
    def fullName = column[String]("full_name")
    def licenseNumber = column[String]("license_number")
    def status = column[String]("status")

    def * = (id, fullName, licenseNumber, status) <> (DriverSnapshot.tupled, DriverSnapshot.unapply)


  }

  val drivers = TableQuery[DriverTable]

  def createTable = {
    MTable.getTables.flatMap { tables =>
      if (!tables.exists(_.name.name == drivers.baseTableRow.tableName)) {
        drivers.schema.create
      } else {
        DBIO.successful(())
      }
    }.transactionally
  }

  def save(snapshot: DriverSnapshot): DBIOAction[Done.type, NoStream, Effect.Write] = {
    drivers.insertOrUpdate(snapshot).map(_ => Done)
  }



}
