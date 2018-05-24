package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import server.routes.{LoginRoute, ParkingRoute}
import service._

sealed trait Config {
  val host: String
  val port: Int
}

final case class InlineConfig(host: String, port: Int) extends Config

object FileConfig extends Config {
  val conf = ConfigFactory.load()
  val host = conf.getString("server.host")
  val port = conf.getInt("server.port")
}

class WebServer(implicit parkingService: ParkingService, loginService: LoginService, config: Config)
  extends HttpApp with CorsSupport with LazyLogging {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val http = Http()

  val loginRouter: LoginRoute = new LoginRoute()
  val parkingRouter: ParkingRoute = new ParkingRoute()

  val route: Route =
    cors() {
      pathPrefix("v1") {
        loginRouter.route ~ parkingRouter.route
      } ~
        pathPrefix("healthcheck") {
          get {
            logger.debug("Healthcheck request")
            complete("OK")
          }
        }
    }

  override def routes: Route = corsSupport(route)

  def start() = startServer(config.host, config.port)
}
