package server.routes

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import service.LoginService

class LoginRouteTest extends FlatSpec with Matchers with ScalatestRouteTest with MockFactory {
  def createEntity(user: String, pass: String) = {
    val entity =
      s"""
         | { "username": "$user",
         |   "password": "$pass"
         | }
        """.stripMargin
    HttpEntity(ContentTypes.`application/json`, entity)
  }

  behavior of "LoginRoute"
  it should "return failure for a POST request to /login with bad credentials" in {
    val fakeLoginService = stub[LoginService]
    fakeLoginService.authenticate _ when("none", "none") returns -1

    val loginRouter = new LoginRoute()(executionContext = system.dispatcher, loginService = fakeLoginService)
    val route = loginRouter.route

    Post("/login").withEntity(createEntity("none", "none")) ~> route ~> check {
      responseAs[String] shouldBe "Wrong username or password"
    }
  }

  it should "return the right driverID for a POST request to /login with good credentials" in {
    val fakeLoginService = stub[LoginService]
    fakeLoginService.authenticate _ when("user", "123") returns 123

    val loginRouter = new LoginRoute()(executionContext = system.dispatcher, loginService = fakeLoginService)
    val route = loginRouter.route

    Post("/login").withEntity(createEntity("user", "123")) ~> route ~> check {
      responseAs[String] shouldBe "123"
    }
  }
}
