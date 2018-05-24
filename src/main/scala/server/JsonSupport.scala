package server

import java.time.Instant
import java.util.Currency

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import domain.requests._
import domain.{Driver, DriverType, Meter}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsNumber, JsString, JsValue, RootJsonFormat}

trait JsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit object InstantJsonFormat extends RootJsonFormat[Instant] {
    def write(c: Instant) = JsNumber(c.getEpochSecond)

    def read(value: JsValue): Instant = value match {
      case JsNumber(number) => Instant.ofEpochSecond(number.toLongExact)
      case _ => throw DeserializationException("Instant expected")
    }
  }

  implicit object CurrencyJsonFormat extends RootJsonFormat[Currency] {
    def write(c: Currency) = JsString(c.toString)

    def read(value: JsValue): Currency = value match {
      case JsString(name) => Currency.getInstance(name)
      case _ => throw DeserializationException("Currency expected")
    }
  }

  implicit object ActionJsonFormat extends RootJsonFormat[Action] {
    def write(c: Action) = JsString(c.name)

    def read(value: JsValue): Action = value match {
      case JsString(name) => Action(name)
      case _ => throw DeserializationException("Action expected")
    }
  }

  implicit object DriverTypeJsonFormat extends RootJsonFormat[DriverType] {
    def write(c: DriverType) = JsString(c.name)

    def read(value: JsValue): DriverType = value match {
      case JsString(name) => DriverType(name)
      case _ => throw DeserializationException("DriverType expected")
    }
  }

  implicit val MeterFormat = jsonFormat6(Meter)
  implicit val DriverFormat = jsonFormat2(Driver)
  implicit val LoginRequestFormat = jsonFormat2(LoginRequest)
  implicit val MeterRequestFormat = jsonFormat2(MeterRequest)
}