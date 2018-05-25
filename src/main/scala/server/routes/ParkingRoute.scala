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
  def handleDriverGet(driverID: Int): Option[Driver] = parkingService.getDriver(driverID)

  def handleMeterGet(driverID: Int): Option[Meter] = parkingService.getMeter(driverID)

  def handleMeterPut(driverID: Int, request: MeterRequest): String = request.action match {
    case Actions.Start =>
      if (parkingService.startMeter(driverID, request.currency)) "OK" else "Meter not found"

    case Actions.Stop =>
      if (parkingService.stopMeter(driverID)) "OK" else "Meter not found"

    case _ =>
      logger.debug("Unknown action " + request.action + " specified in put request at /meter/" + driverID)
      s"Unknown action ${request.action}"
  }

  val route = cors() {
    pathPrefix("driver" / IntNumber) {
      driverID =>
        get {
          complete {
            handleDriverGet(driverID)
          }
        }
    } ~
      pathPrefix("meter" / IntNumber) {
        driverID =>
          get {
            complete {
              handleMeterGet(driverID)
            }
          } ~
            (put | parameter('method ! "put")) {
              entity(as[MeterRequest]) {
                request =>
                  complete {
                    handleMeterPut(driverID, request)
                  }
              }
            }
      }
  }
}
