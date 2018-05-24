package server.routes

import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import com.typesafe.scalalogging.LazyLogging
import domain.requests.{Actions, MeterRequest}
import domain.{Driver, Meter}
import server.{CorsSupport, JsonSupport}
import service.ParkingService

import scala.concurrent.ExecutionContext

class ParkingRoute(implicit val executionContext: ExecutionContext, implicit val parkingService: ParkingService)
  extends JsonSupport with CorsSupport with LazyLogging {
  def handleDriverGet(driverID: Int, complete: Driver => Unit) = {
    val driver = parkingService.getDriver(driverID)
    complete(driver)
  }

  def handleMeterGet(driverID: Int, complete: Meter => Unit) = {
    val meter = parkingService.getMeter(driverID)
    complete(meter)
  }

  def handleMeterPut(driverID: Int, request: MeterRequest) = request.action match {
    case Actions.Start => parkingService.startMeter(driverID, request.currency)
    case Actions.Stop => parkingService.stopMeter(driverID)
    case _ => {
      logger.debug("Unknown action " + request.action + " specified in put request at /meter/" + driverID)
      throw new IllegalArgumentException("Unknown action in request")
    }
  }

  val route = cors() {
    pathPrefix("driver" / IntNumber) {
      driverID =>
        get {
          completeWith(instanceOf[Driver]) {
            completer =>
              handleDriverGet(driverID, completer)
          }
        }
    } ~
      pathPrefix("meter" / IntNumber) {
        driverID =>
          get {
            completeWith(instanceOf[Meter]) {
              completer =>
                handleMeterGet(driverID, completer)
            }
          } ~
            (put | parameter('method ! "put")) {
              entity(as[MeterRequest]) {
                request =>
                  complete {
                    handleMeterPut(driverID, request)
                    "OK"
                  }
              }
            }
      }
  }
}
