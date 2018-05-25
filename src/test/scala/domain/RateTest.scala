package domain

import org.scalatest.{FlatSpec, Matchers}

class RateTest extends FlatSpec with Matchers {
  behavior of "Rates"
  it should "return a rate for the PLN currency" in {
    Rates.getRate(Currencies.PLN) shouldBe a [Rate]
  }
  behavior of "PLN regular rate"
  it should "cost 1 PLN for the 1st hour" in {
    val rate = Rates.getRate(Currencies.PLN)
    rate.getRegular(0) shouldBe 1
  }

  it should "cost 2 PLN for the 2nd hour" in {
    val rate = Rates.getRate(Currencies.PLN)
    rate.getRegular(1) shouldBe 2
  }

  it should "cost 1.5x more than the previous one for the 3rd and next hour" in {
    val rate = Rates.getRate(Currencies.PLN)
    def expectedRate(hour: Int): BigDecimal = hour match {
      case 1 => BigDecimal("2")
      case other => expectedRate(other - 1) * BigDecimal("1.5")
    }
    def expectedValues = Range(2, 8).map(hour => expectedRate(hour))
    def actualValues = Range(2, 8).map(hour => rate.getRegular(hour))

    actualValues shouldBe expectedValues
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
    def expectedRate(hour: Int): BigDecimal = hour match {
      case 1 => BigDecimal("2")
      case other => expectedRate(other - 1) * BigDecimal("1.2")
    }
    def expectedValues = Range(2, 8).map(hour => expectedRate(hour))
    def actualValues = Range(2, 8).map(hour => rate.getVIP(hour))

    actualValues shouldBe expectedValues
  }
}
