package domain.requests

import java.util.Currency

case class Action(name: String)

object Actions {

  val Start = Action("start")

  val Stop = Action("stop")

}

// currency matters only in case action is "start"
// this class represents a PUT request to start/stop a meter
case class MeterRequest(action: Action, currency: Currency)
