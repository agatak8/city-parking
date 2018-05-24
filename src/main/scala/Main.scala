import server.{FileConfig, WebServer}
import service.{LoginService, ParkingService}
import database.MockDatabase

object Main {
  implicit val database = MockDatabase
  implicit val loginService = new LoginService()
  implicit val parkingService = new ParkingService()
  implicit val config = FileConfig
  val server = new WebServer()

  def main(args: Array[String]): Unit = {
    server.start()
  }
}
