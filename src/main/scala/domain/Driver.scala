package domain

final case class DriverType(name: String)

object DriverTypes {

  val Regular = DriverType("regular")

  val VIP = DriverType("vip")

}

final case class Driver(id: Int, driverType: DriverType)