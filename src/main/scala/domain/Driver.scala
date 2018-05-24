package domain

case class DriverType(name: String)

object DriverTypes {

  val Regular = DriverType("regular")

  val VIP = DriverType("vip")

}

case class Driver(id: Int, driverType: DriverType)