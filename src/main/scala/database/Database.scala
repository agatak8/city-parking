package database

import domain._

trait Database {
  def getDriver(id: Int): Option[Driver]

  def getMeter(driverID: Int): Option[Meter]

  def putMeter(meter: Meter): Unit

  def getUser(username: String): Option[User]
}
