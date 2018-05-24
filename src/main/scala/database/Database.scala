package database

import domain._

trait Database {
  def getDriver(id: Int): Driver

  def getMeter(driverID: Int): Meter

  def putMeter(meter: Meter)

  def getUser(username: String): User
}
