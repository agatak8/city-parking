package database

import java.time.Instant

import com.typesafe.scalalogging.LazyLogging
import domain.Currencies.PLN
import domain.{Driver, DriverTypes, Meter, User}

object MockDatabase extends Database with LazyLogging {
  val drivers = Array(Driver(0, DriverTypes.Regular),
    Driver(1, DriverTypes.VIP),
    Driver(2, DriverTypes.Regular))

  val meters = drivers.map(driver => Meter(driver.id, started = false, Instant.MIN, 0, PLN, 0))

  val users = Map(("john", User("john", "123", 0)),
    ("jane", User("jane", "jane123", 1)),
    ("jamie", User("jamie", "jj11", 2)))

  def getDriver(id: Int) = {
    if(id >= 0 && id < drivers.length) Some(drivers(id))
    else None
  }

  def getMeter(driverID: Int) = {
    if(driverID >= 0 && driverID < drivers.length) Some(meters(driverID))
    else None
  }

  def putMeter(meter: Meter): Unit = {
    meters(meter.driverID) = meter
  }

  def getUser(username: String) = {
    if(users.contains(username)) Some(users(username))
    else None
  }
}
