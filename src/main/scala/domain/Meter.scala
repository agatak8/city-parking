package domain

import java.time.Instant
import java.util.Currency

final case class Meter(driverID: Int,
                       started: Boolean,
                       startTime: Instant, // set when meter is started
                       elapsedHours: Int,
                       currency: Currency,
                       fare: BigDecimal)
