package domain

import java.util.Currency

sealed trait Rate {
  def getRegular(hour: Int): BigDecimal

  def getVIP(hour: Int): BigDecimal

  // this will return the fare total for all the elapsedHours
  // that is, it will sum up the fares for all the hours
  def getFare(driverType: DriverType, elapsedHours: Int): BigDecimal = {
    val hourRange = List.range(0, elapsedHours + 1)
    val farePerHour: Int => BigDecimal = driverType match {
      case DriverTypes.Regular => getRegular
      case DriverTypes.VIP => getVIP
      case _ => throw new IllegalArgumentException("Fare not defined for this type of driver")
    }
    hourRange.map(hour => farePerHour(hour)).sum
  }
}

object Rates {

  // 0 means the first hour has started, but the second hasn't
  object PLN extends Rate {
    def getRegular(hour: Int): BigDecimal = hour match {
      case 0 => BigDecimal("1.0")
      case 1 => BigDecimal("2.0")
      case other => getRegular(other - 1) * BigDecimal("1.5")
    }

    def getVIP(hour: Int): BigDecimal = hour match {
      case 0 => BigDecimal("0.0")
      case 1 => BigDecimal("2.0")
      case other => getVIP(other - 1) * BigDecimal("1.2")
    }
  }

  def getRate(currency: Currency): Rate = currency match {
    case Currencies.PLN => Rates.PLN
    case other: Currency => throw new IllegalArgumentException("Unimplemented currency: " + other.toString)
  }
}