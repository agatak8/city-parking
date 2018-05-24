package domain

import org.scalatest.FlatSpec

class RateTest extends FlatSpec {
  behavior of "Rates"
  it should "return a rate for the PLN currency" in {
    assert(Rates.getRate(Currencies.PLN).isInstanceOf[Rate])
  }
  behavior of "PLN regular rate"
  it should "cost 1 PLN for the 1st hour" in {
    val rate = Rates.getRate(Currencies.PLN)
    assert(rate.getRegular(0) == 1)
  }

  it should "cost 2 PLN for the 2nd hour" in {
    val rate = Rates.getRate(Currencies.PLN)
    assert(rate.getRegular(1) == 2)
  }

  it should "cost 1.5x more than the previous one for the 3rd and next hour" in {
    val rate = Rates.getRate(Currencies.PLN)
    val expectedValues = Range(1, 5).map {
      case 1 => BigDecimal("2")
      case 2 => BigDecimal("3")
      case 3 => BigDecimal("4.5")
      case 4 => BigDecimal("6.75")
    }
    assert(Range(1, 5).map(hour => rate.getRegular(hour)) == expectedValues)
  }

  behavior of "PLN VIP rate"
  it should "cost 0 PLN for the 1st hour" in {
    val rate = Rates.getRate(Currencies.PLN)
    assert(rate.getVIP(0) == 0)
  }

  it should "cost 2 PLN for the 2nd hour" in {
    val rate = Rates.getRate(Currencies.PLN)
    assert(rate.getVIP(1) == 2)
  }

  it should "cost 1.2x more than the previous one for the 3rd and next hour" in {
    val rate = Rates.getRate(Currencies.PLN)
    val expectedValues = Range(1, 5).map {
      case 1 => BigDecimal("2")
      case 2 => BigDecimal("2.4")
      case 3 => BigDecimal("2.88")
      case 4 => BigDecimal("3.456")
    }
    assert(Range(1, 5).map(hour => rate.getVIP(hour)) == expectedValues)
  }
}
