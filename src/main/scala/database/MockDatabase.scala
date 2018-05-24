package database

import java.time.Instant
import java.util.NoSuchElementException

import com.typesafe.scalalogging.LazyLogging
import domain.Currencies.PLN
import domain.{Driver, DriverTypes, Meter, User}

object MockDatabase extends Database with LazyLogging {
  val drivers = Array(Driver(0, DriverTypes.Regular),
    Driver(1, DriverTypes.VIP),
    Driver(2, DriverTypes.Regular))

  val meters = drivers.map(driver => Meter(driver.id, false, Instant.MIN, 0, PLN, 0))

  val users = Map(("john", User("john", "123", 0)),
    ("jane", User("jane", "jane123", 1)),
    ("jamie", User("jamie", "jj11", 2)))

  def getDriver(id: Int) = {
    try {
      drivers(id)
    }
    catch {
      case e: ArrayIndexOutOfBoundsException => {
        throw new NoSuchElementException("Driver not found")
      }
    }
  }

  def getMeter(driverID: Int) = {
    try {
      meters(driverID)
    }
    catch {
      case e: ArrayIndexOutOfBoundsException => {
        throw new NoSuchElementException("Meter not found")
      }
    }
  }

  def putMeter(meter: Meter) = {
    meters(meter.driverID) = meter
  }

  def getUser(username: String) = {
    try {
      users(username)
    }
    catch {
      case e: ArrayIndexOutOfBoundsException => {
        throw new NoSuchElementException("User not found")
      }
    }
  }
}
