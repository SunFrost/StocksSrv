import java.nio.ByteBuffer
import java.util.{Base64, Date}

import Stocks.Deal
import akka.util.ByteString

object UpstreamDecoder {

  def BB2Str(byteBuffer: ByteBuffer): String = {
    import java.nio.charset.Charset
    val chars = Charset.forName("UTF-8").decode(byteBuffer)
    new String(chars.array())
  }

  def decode(data: ByteString): Deal = {
    val bb = data.toArray
    val len = ByteBuffer.wrap(bb, 0, 2).getShort
    val mills = ByteBuffer.wrap(bb, 2, 8).getLong
    val ticker_len = ByteBuffer.wrap(bb, 10, 2).getShort
    val ticker = BB2Str(ByteBuffer.wrap(bb, 12, ticker_len))
    val price = ByteBuffer.wrap(bb, 12 + ticker_len, 8).getDouble
    val size = ByteBuffer.wrap(bb, 12 + ticker_len + 8, 4).getInt
    Deal(new Date(mills), ticker, price, size)
  }

}
