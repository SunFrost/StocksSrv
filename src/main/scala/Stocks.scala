import java.util.Date

object Stocks {
  case class Deal(timestamp: Date, ticker: String, price: BigDecimal, size: Int)
  case class Candle(ticker: String, timestamp: Date, open: BigDecimal, high: BigDecimal, low: BigDecimal, close: BigDecimal, volume: Int)

  def extractCandles(timestamp: Date, deals: Seq[Deal]): Seq[Candle] = {
    deals
      .groupBy(_.ticker)
      .map({ case (ticker, dls) =>
        val (low, high, vol) = dls.foldLeft((BigDecimal(Integer.MAX_VALUE), BigDecimal(0), 0)) { case ((min, max, sum), deal) =>
          (if (deal.price < min) deal.price else min, if (deal.price > max) deal.price else max, sum + deal.size)
        }
        Candle(ticker, timestamp, dls.head.price, high, low, dls.last.price, vol)
      }).toSeq
  }

}
