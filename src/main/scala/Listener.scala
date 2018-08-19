import akka.actor.Actor
import akka.io.Tcp.Connected
import akka.util.ByteString

class Listener extends Actor {

  def receive = {
    case Connected(remote, local) =>
      println(s"Connected: $remote $local")

    case s: String =>
      println(s"CMD: $s")

    case data: ByteString =>
      println(data.utf8String)

  }
}
