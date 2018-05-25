package service

import java.util.NoSuchElementException

import database.Database
import domain.User
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

class LoginServiceTest extends FlatSpec with MockFactory with Matchers {
  behavior of "LoginService"
  it should "not authenticate a non existing user" in {
    val fakeDb = stub[Database]
    val service = new LoginService()(database = fakeDb)

    fakeDb.getUser _ when "user" returns None

    service.authenticate("user", "user") shouldBe -1
  }

  it should "not authenticate an existing user with the wrong password" in {
    val fakeDb = stub[Database]
    val service = new LoginService()(database = fakeDb)

    fakeDb.getUser _ when "user" returns Some(User("user", "123", 123))

    service.authenticate("user", "user") shouldBe -1
  }

  it should "authenticate an existing user with the right password" in {
    val fakeDb = stub[Database]
    val service = new LoginService()(database = fakeDb)

    fakeDb.getUser _ when "user" returns Some(User("user", "123", 123))

    service.authenticate("user", "123") should not be -1
  }

  it should "return the user's driverID when authenticating with the right password" in {
    val fakeDb = stub[Database]
    val service = new LoginService()(database = fakeDb)

    fakeDb.getUser _ when "user" returns Some(User("user", "123", 444))

    service.authenticate("user", "123") shouldBe 444
  }
}
