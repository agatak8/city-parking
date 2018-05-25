package server.routes

import java.time.Instant

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import domain.{Currencies, Driver, DriverTypes, Meter}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import server.JsonSupport
import service.ParkingService

class ParkingRouteTest extends FlatSpec with Matchers with ScalatestRouteTest with MockFactory with JsonSupport {
  val meter = Meter(123, started = false, Instant.MIN, 0, Currencies.PLN, 0)
  val driver = Driver(444, DriverTypes.VIP)

  def createEntity(action: String, currency: String) = {
    val entity =
      s"""
         | { "action": "$action",
         |   "currency": "$currency"
         | }
        """.stripMargin
    HttpEntity(ContentTypes.`application/json`, entity)
  }

  behavior of "ParkingRoute"
  it should "return failure message for a GET request at /driver/id with a non existent id" in {
    val fakeParkingService = stub[ParkingService]
    fakeParkingService.getDriver _ when 555 throws new NoSuchElementException("Not found")

    val parkingRouter = new ParkingRoute()(executionContext = system.dispatcher, parkingService = fakeParkingService)
    val route = parkingRouter.route

    Get("/driver/555") ~> route ~> check {
      responseAs[String] should not be "OK"
    }
  }

  it should "return the specified meter for a GET request at /meter/id" in {
    val fakeParkingService = stub[ParkingService]
    fakeParkingService.getMeter _ when 123 returns Some(meter)

    val parkingRouter = new ParkingRoute()(executionContext = system.dispatcher, parkingService = fakeParkingService)
    val route = parkingRouter.route

    Get("/meter/123") ~> route ~> check {
      responseAs[Meter] shouldBe meter
    }
  }

  it should "return failure for a GET request at /meter/id with a non existent id" in {
    val fakeParkingService = stub[ParkingService]
    fakeParkingService.getMeter _ when 555 throws new NoSuchElementException("Not found")

    val parkingRouter = new ParkingRoute()(executionContext = system.dispatcher, parkingService = fakeParkingService)
    val route = parkingRouter.route

    Get("/meter/555") ~> route ~> check {
      status.isFailure() shouldBe true
    }
  }

  it should "return the specified driver for a GET request at /driver/id" in {
    val fakeParkingService = stub[ParkingService]
    fakeParkingService.getDriver _ when 123 returns Some(driver)

    val parkingRouter = new ParkingRoute()(executionContext = system.dispatcher, parkingService = fakeParkingService)
    val route = parkingRouter.route

    Get("/driver/123") ~> route ~> check {
      responseAs[Driver] shouldBe driver
    }
  }

  it should "try to stop the specified meter with a PUT request at /meter/id with stop action" in {
    val fakeParkingService = stub[ParkingService]
    fakeParkingService.getMeter _ when 123 returns Some(meter)

    val parkingRouter = new ParkingRoute()(executionContext = system.dispatcher, parkingService = fakeParkingService)
    val route = parkingRouter.route

    Put("/meter/123").withEntity(createEntity("stop", "PLN")) ~> route ~> check {
      fakeParkingService.stopMeter _ verify 123
    }
  }

  it should "try to start the specified meter with a PUT request at /meter/id with start action" in {
    val fakeParkingService = stub[ParkingService]
    fakeParkingService.getMeter _ when 123 returns Some(meter)

    val parkingRouter = new ParkingRoute()(executionContext = system.dispatcher, parkingService = fakeParkingService)
    val route = parkingRouter.route

    Put("/meter/123").withEntity(createEntity("start", "PLN")) ~> route ~> check {
      fakeParkingService.startMeter _ verify(123, Currencies.PLN)
    }
  }
}
