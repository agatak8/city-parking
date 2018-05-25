package service

import java.time.Instant
import java.util.Currency

import com.typesafe.scalalogging.LazyLogging
import database.Database
import domain.{Driver, Meter, Rates}

class ParkingService(implicit database: Database) extends LazyLogging {
  // if the meter was started, it will recalculate and update the elapsedHours and fare
  def getMeter(id: Int): Option[Meter] = {
    val dbMeter = database.getMeter(id)
    dbMeter match {
      case Some(meter) => {
        if (meter.started) {
          val newMeter = recalculate(meter)
          logger.debug(s"Updating meter $id")
          database.putMeter(newMeter)
          logger.debug(s"Retrieved updated meter $newMeter")
          Some(newMeter)
        }
        else {
          logger.debug(s"Retrieved meter $meter")
          dbMeter
        }
      }
      case None => {
        logger.debug(s"Could not find meter $id")
        dbMeter
      }
    }
  }

  // recalculates the meter's elapsed time and fare and returns a new meter with updated values
  def recalculate(meter: Meter): Meter = {
    val dbDriver = getDriver(meter.driverID)
    dbDriver match {
      case None => {
        logger.debug(s"Could not recalculate meter $meter because the driver was not found")
        meter
      }
      case Some(driver) => {
        val driverType = driver.driverType
        val elapsedHours = ((Instant.now.getEpochSecond - meter.startTime.getEpochSecond) / 3600).asInstanceOf[Int]
        val rate = Rates.getRate(meter.currency)
        val fare = rate.getFare(driverType, elapsedHours)
        meter.copy(elapsedHours = elapsedHours, fare = fare)
      }
    }
  }

  def getDriver(id: Int): Option[Driver] = {
    val driver = database.getDriver(id)
    driver match {
      case Some(_) => logger.debug(s"Retrieved driver $driver")
      case None => logger.debug(s"Could not find driver $id")
    }
    driver
  }

  // resets the meter and sets it to started
  // it also resets the fare for testing purposes
  def startMeter(id: Int, currency: Currency): Boolean = {
    val dbMeter = database.getMeter(id)
    dbMeter match {
      case Some(meter) => {
        val newMeter = meter.copy(started = true, startTime = Instant.now(), elapsedHours = 0, fare = 0, currency =
          currency)
        logger.debug(s"Starting meter $id at time ${newMeter.startTime}")
        database.putMeter(newMeter)
        true
      }
      case None => {
        logger.debug(s"Could not start meter $id because it was not found")
        false
      }
    }
  }

  def stopMeter(id: Int): Boolean = {
    val dbMeter = database.getMeter(id)
    dbMeter match {
      case Some(meter) => {
        val newMeter = meter.copy(started = false)
        logger.debug(s"Stopping meter $id")
        database.putMeter(newMeter)
        true
      }
      case None => {
        logger.debug(s"Could not stop meter $id because it was not found")
        false
      }
    }
  }
}
