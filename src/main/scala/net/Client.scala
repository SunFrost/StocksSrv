package net

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef}
import akka.io.{IO, Tcp}
import akka.util.ByteString
import akka.io.Tcp._

class Client(remote: InetSocketAddress, listener: ActorRef) extends Actor {
  import context.system
  IO(Tcp) ! Connect(remote)

  def receive = {
    case CommandFailed(_: Connect) =>
      listener ! "connect failed"
      context.stop(self)

    case con @ Connected(remote, local) =>
      val connection = sender()
      connection ! Register(self)
      listener ! con

      context.become {
        case data: ByteString =>
          connection ! Write(data)
        case CommandFailed(w: Write) =>
          listener ! "write failed"
        case Received(data) =>
          listener ! data
        case "close" =>
          connection ! Close
        case _: ConnectionClosed =>
          listener ! "connection closed"
          context.stop(self)
      }
  }
}