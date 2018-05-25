package server

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler, Route}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings

trait CorsSupport {
  // Your rejection handler
  val rejectionHandler = corsRejectionHandler withFallback RejectionHandler.default

  // Your exception handler
  val exceptionHandler = ExceptionHandler {
    case e: IllegalArgumentException => complete(StatusCodes.BadRequest -> e.getMessage)
  }

  val handleErrors = handleRejections(rejectionHandler) & handleExceptions(exceptionHandler)

  val settings = CorsSettings.defaultSettings.withAllowGenericHttpRequests(true)

  def corsSupport(route: Route): Route = {
    handleErrors {
      cors(settings) {
        handleErrors {
          route
        }
      }
    }
  }
}