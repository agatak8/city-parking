package server.routes

import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import com.typesafe.scalalogging.LazyLogging
import domain.requests.LoginRequest
import server.{CorsSupport, JsonSupport}
import service.LoginService

import scala.concurrent.ExecutionContext

class LoginRoute(implicit val executionContext: ExecutionContext, implicit val loginService: LoginService)
  extends JsonSupport with CorsSupport with LazyLogging {
  def handleLoginPost(request: LoginRequest) = {
    val driverID = loginService.authenticate(request.username, request.password)
    if (driverID < 0) "Wrong username or password"
    else driverID.toString
  }

  val route = cors() {
    pathPrefix("login") {
      post {
        entity(as[LoginRequest]) {
          request =>
            complete {
              handleLoginPost(request)
            }
        }
      }
    }
  }
}

