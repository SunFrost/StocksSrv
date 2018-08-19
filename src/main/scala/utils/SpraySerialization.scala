package utils

import java.text.SimpleDateFormat
import java.util.Date

import akka.util.ByteString
import logic.Stocks.Candle
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, _}

import scala.util.Try

object SpraySerialization extends DefaultJsonProtocol {

  implicit object DateFormat extends JsonFormat[Date] {
    def write(date: Date) = JsString(dateToIsoString(date))

    def read(json: JsValue): Date = json match {
      case JsString(rawDate) => parseIsoDateString(rawDate).get
    }

    private val localIsoDateFormatter = new ThreadLocal[SimpleDateFormat] {
      override def initialValue() = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    }

    private def dateToIsoString(date: Date) =
      localIsoDateFormatter.get().format(date)

    private def parseIsoDateString(date: String): Option[Date] =
      Try {
        localIsoDateFormatter.get().parse(date)
      }.toOption
  }

  implicit val candleFormat = jsonFormat7(Candle)

  def serialize(candles: Seq[Candle]): ByteString = {
    val s = candles.map(_.toJson.toString).foldLeft(""){_+_+"\n"}
    ByteString(s)
  }

}
