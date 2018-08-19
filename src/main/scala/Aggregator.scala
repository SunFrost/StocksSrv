import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import Stocks.{Candle, Deal}
import akka.actor.{Actor, ActorRef, Timers}
import akka.util.ByteString

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

case class Reg()
case class Unreg()
case class CurCandles(candles: Seq[Candle])

case class QCandles()//Q - Query
case class ACandles(candles: Seq[Candle])//A - Answer

class Aggregator extends Actor with Timers {
  private object StepTimer
  private object StepTick

  private val STEP_DUR = 60//sec
  private val STORE_STEP_COUNT = 10//steps
  private var targets = ListBuffer[ActorRef]()

  val candles = mutable.ListBuffer[Candle]()
  val deals = mutable.ListBuffer[Deal]()

  setTimer()

  def receive = {

    case Reg() =>
      targets += sender()

    case Unreg() =>
      targets -= sender()

    case data: ByteString =>
      deals += UpstreamDecoder.decode(data)

    case StepTick =>
      val cal = Calendar.getInstance()
      cal.add(Calendar.SECOND, -STEP_DUR)

      val curCandles = Stocks.extractCandles(cal.getTime, deals)
      deals.clear()

      targets.foreach(_ ! CurCandles(curCandles))

      candles ++= curCandles

      val calLimit = Calendar.getInstance()
      calLimit.add(Calendar.SECOND, - STEP_DUR * (STORE_STEP_COUNT + 1))
      trunc(candles, calLimit.getTime)

      setTimer()

    case QCandles() =>
      sender() ! ACandles(candles)
  }

  def trunc(candles: ListBuffer[Candle], date: Date) {//Требует хронологического порядка поступления данных от upstream.py (timestamp)
    val count = candles.count(_.timestamp.before(date))
    println(s"beg ${candles.size}")
    candles.trimStart(count)
    println(s"end ${candles.size}")
    println(candles)
  }

  def setTimer() {
    val msAlign = (STEP_DUR * 1000) - System.currentTimeMillis % (STEP_DUR * 1000)
    timers.startSingleTimer(StepTimer, StepTick, msAlign milliseconds)
  }

}