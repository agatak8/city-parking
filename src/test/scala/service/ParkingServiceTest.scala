package service

import java.time.Instant

import database.Database
import domain._
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

class ParkingServiceTest extends FlatSpec with MockFactory with Matchers {
  behavior of "ParkingService"
  it should "fetch the specified driver from the database" in {
    val fakeDb = stub[Database]
    val d1 = Driver(444, DriverTypes.Regular)
    val d2 = Driver(555, DriverTypes.VIP)

    fakeDb.getDriver _ when 444 returns Some(d1)
    fakeDb.getDriver _ when 555 returns Some(d2)

    val service = new ParkingService()(database = fakeDb)

    service.getDriver(444).get should equal(d1)
    service.getDriver(555).get should equal(d2)
  }

  it should "fetch an identical stopped meter from the database" in {
    val fakeDb = stub[Database]
    val meter = Meter(123, started = false, Instant.ofEpochSecond(456), 5, Currencies.PLN, BigDecimal("7"))
    fakeDb.getMeter _ when 123 returns Some(meter)

    val service = new ParkingService()(database = fakeDb)

    service.getMeter(123).get should equal(meter)
  }
  it should "fetch a nearly identical started meter from the database" in {
    val fakeDb = stub[Database]
    val driver = Driver(123, DriverTypes.Regular)
    val meter = Meter(123, started = true, Instant.now(), 5, Currencies.PLN, BigDecimal("7"))
    fakeDb.getMeter _ when 123 returns Some(meter)
    fakeDb.getDriver _ when 123 returns Some(driver)

    val service = new ParkingService()(database = fakeDb)

    // since meter was set to started, elapsedTime and fare could change
    service.getMeter(123).get should have(
      'driverID (meter.driverID),
      'started (meter.started),
      'currency (meter.currency)
    )
  }

  it should "correctly increase the fare and elapsed hours for a started meter" in {
    val fakeDb = stub[Database]
    val meter = Meter(123, started = true, Instant.ofEpochSecond(Instant.now.getEpochSecond - 2 * 3601), 0,
      Currencies.PLN, BigDecimal("0"))
    val driver = Driver(123, DriverTypes.Regular)
    val service = new ParkingService()(database = fakeDb)

    fakeDb.getMeter _ when 123 returns Some(meter)
    fakeDb.getDriver _ when 123 returns Some(driver)

    val newMeter = service.getMeter(meter.driverID)
    newMeter should not be empty
    newMeter.get.elapsedHours shouldBe 2
    newMeter.get.fare shouldBe domain.Rates.getRate(Currencies.PLN).getFare(driver.driverType, 2)
  }

  it should "stop the specified meter in the database" in {
    val fakeDb = stub[Database]
    val meter = Meter(123, started = true, Instant.ofEpochSecond(0), 0, Currencies.PLN, BigDecimal("0"))
    val service = new ParkingService()(database = fakeDb)

    fakeDb.getMeter _ when 123 returns Some(meter)
    service.stopMeter(123)
    fakeDb.putMeter _ verify where { meter: Meter => !meter.started }
  }

  it should "start the specified meter in the database with the current time" in {
    val fakeDb = stub[Database]
    val meter = Meter(123, started = false, Instant.ofEpochSecond(0), 0, Currencies.PLN, BigDecimal("0"))
    val service = new ParkingService()(database = fakeDb)
    val startTime = Instant.now()

    def timeEqualityPredicate(t1: Instant, t2: Instant) = {
      t1.equals(t2) ||
        (t1.isAfter(t2.minusSeconds(10)) && t1.isBefore(t2.plusSeconds(10)))
    }

    fakeDb.getMeter _ when 123 returns Some(meter)
    service.startMeter(123, meter.currency)
    fakeDb.putMeter _ verify where {
      meter: Meter => meter.started && timeEqualityPredicate(meter.startTime, startTime)
    }
  }
}
