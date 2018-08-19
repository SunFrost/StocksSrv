package net

import java.net.InetSocketAddress

import akka.actor.{Actor, Props}
import akka.io.{IO, Tcp}
import Tcp._

class Server extends Actor {
  import context.system
  IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", 5556))

  def receive = {
    case b @ Bound(localAddress) =>
      context.parent ! b

    case CommandFailed(_: Bind) =>
      context.stop(self)

    case Connected(remote, local) =>
      val connection = sender()
      val handler = context.actorOf(Props(classOf[ConnectionHandler], connection, remote, local))
      connection ! Register(handler, keepOpenOnPeerClosed = false)

  }

}
