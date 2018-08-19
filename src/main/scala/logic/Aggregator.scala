package logic

import java.util.{Calendar, Date}

import Stocks.{Candle, Deal}
import akka.actor.{Actor, ActorRef, Timers}
import akka.io.Tcp.Connected
import akka.util.ByteString
import logic.Aggregator._
import utils.UpstreamDecoder

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

object Aggregator {
  case class Reg()
  case class Unreg()
  case class CurCandles(candles: Seq[Candle])
  case class QCandles()//Q - Query
  case class ACandles(candles: Seq[Candle])//A - Answer
}

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
    case Connected(remote, local) =>
      println(s"connected to upstream: $remote $local")

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
      trunc(candles, getLimitDate(STEP_DUR, STORE_STEP_COUNT))

      setTimer()

    case QCandles() =>
      sender() ! ACandles(candles)
  }

  def getLimitDate(duration: Int, stepCount: Int) : Date = {
    val calLimit = Calendar.getInstance()
    calLimit.add(Calendar.SECOND, - duration * (stepCount + 1))
    calLimit.getTime
  }

  def trunc(candles: ListBuffer[Candle], date: Date) {
    //Требует хронологического порядка поступления данных от upstream.py (timestamp)
    val count = candles.count(_.timestamp.before(date))
    candles.trimStart(count)
  }

  def setTimer() {
    timers.startSingleTimer(StepTimer, StepTick, Stocks.alignTime(STEP_DUR) milliseconds)
  }

}