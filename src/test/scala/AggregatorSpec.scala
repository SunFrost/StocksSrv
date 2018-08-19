import java.util.Calendar

import logic.Stocks.Candle
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.ByteString
import logic.Aggregator._
import logic._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.math.BigDecimal

class AggregatorSpec() extends TestKit(ActorSystem("testSystem")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An Aggregator actor" must {

    "must reply at the beginning of the minute" in {
//      Deal(Sun Aug 19 13:12:29 YEKT 2018,SPY,91.65,500)
//      Deal(Sun Aug 19 13:12:30 YEKT 2018,SPY,90.6,200)
//      Deal(Sun Aug 19 13:12:31 YEKT 2018,AAPL,98.9,4300)
//      Deal(Sun Aug 19 13:12:32 YEKT 2018,SPY,94.25,700)

//      CurCandles(List(
//      Candle(AAPL,Sun Aug 19 13:56:00 YEKT 2018,98.9,98.9,98.9,98.9,4300),
//      Candle(SPY,Sun Aug 19 13:56:00 YEKT 2018,91.65,94.25,90.6,94.25,1400)
//      ))

      val aggr = system.actorOf(Props[Aggregator])
      aggr ! ByteString(0, 25, 0, 0, 1, 101, 81, 62, 27, 18, 0, 3, 83, 80, 89, 64, 86, -23, -103, -103, -103, -103, -102, 0, 0, 1, -12)
      aggr ! ByteString(0, 25, 0, 0, 1, 101, 81, 62, 31, 43, 0, 3, 83, 80, 89, 64, 86, -90, 102, 102, 102, 102, 102, 0, 0, 0, -56)
      aggr ! ByteString(0, 26, 0, 0, 1, 101, 81, 62, 33, -65, 0, 4, 65, 65, 80, 76, 64, 88, -71, -103, -103, -103, -103, -102, 0, 0, 16, -52)
      aggr ! ByteString(0, 25, 0, 0, 1, 101, 81, 62, 40, -63, 0, 3, 83, 80, 89, 64, 87, -112, 0, 0, 0, 0, 0, 0, 0, 2, -68)
      aggr ! Reg()

      val cal = Calendar.getInstance()
      cal.set(Calendar.SECOND, 0)
      val alignedTimestamp = cal.getTime.toString

      val aaplNums = (BigDecimal(98.9),BigDecimal(98.9),BigDecimal(98.9),BigDecimal(98.9), 4300)
      val spyNums = (BigDecimal(91.65),BigDecimal(94.25),BigDecimal(90.6),BigDecimal(94.25), 1400)

      expectMsgPF(61 seconds) { //почему просто не expectMsg ? Потому что timestamp отличается на миллисекунды, поэтому сравниваем по toString
        case CurCandles(List(Candle("AAPL", aaplTimestamp, aaplNums._1, aaplNums._2, aaplNums._3, aaplNums._4, aaplNums._5),
             Candle("SPY", spyTimestamp, spyNums._1, spyNums._2, spyNums._3, spyNums._4, spyNums._5)))
          if aaplTimestamp.toString == alignedTimestamp && spyTimestamp.toString == alignedTimestamp => true
      }
    }
  }
}
