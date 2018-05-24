package service

import java.time.Instant
import java.util.Currency

import com.typesafe.scalalogging.LazyLogging
import database.Database
import domain.{Driver, Meter, Rates}

class ParkingService(implicit database: Database) extends LazyLogging {
  // if the meter was started, it will recalculate and update the elapsedHours and fare
  def getMeter(id: Int): Meter = {
    val meter = database.getMeter(id)
    if (meter.started) {
      val newMeter = recalculate(meter)
      logger.debug(s"Updating meter $id")
      database.putMeter(newMeter)
      logger.debug(s"Retrieved updated meter $newMeter")
      newMeter
    }
    else {
      logger.debug(s"Retrieved meter $meter")
      meter
    }
  }

  // recalculates the meter's elapsed time and fare and returns a new meter with updated values
  def recalculate(meter: Meter): Meter = {
    val driverType = getDriver(meter.driverID).driverType
    val elapsedHours = ((Instant.now.getEpochSecond - meter.startTime.getEpochSecond) / 3600).asInstanceOf[Int]
    val rate = Rates.getRate(meter.currency)
    val fare = rate.getFare(driverType, elapsedHours)

    meter.copy(elapsedHours = elapsedHours, fare = fare)
  }

  def getDriver(id: Int): Driver = {
    val driver = database.getDriver(id)
    logger.debug(s"Retrieved driver $driver")
    driver
  }

  // resets the meter and sets it to started
  // it also resets the fare for testing purposes
  def startMeter(id: Int, currency: Currency): Unit = {
    val meter = database.getMeter(id)
    val newMeter = meter.copy(started = true, startTime = Instant.now(), elapsedHours = 0, fare = 0, currency =
      currency)
    logger.debug(s"Starting meter $id at time ${newMeter.startTime}")
    database.putMeter(newMeter)
  }

  def stopMeter(id: Int): Unit = {
    val meter = database.getMeter(id)
    val newMeter = meter.copy(started = false)
    logger.debug(s"Stopping meter $id")
    database.putMeter(newMeter)
  }
}
