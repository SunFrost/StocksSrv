package utils

import java.net.InetSocketAddress

object InetSocketAddressInst {
  def apply(host: String, port: Int) = new InetSocketAddress(host, port)
}
