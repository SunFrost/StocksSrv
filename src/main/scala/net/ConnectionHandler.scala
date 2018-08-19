package net

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef}
import akka.io.Tcp._
import logic.Aggregator._
import utils.SpraySerialization

class ConnectionHandler(connection: ActorRef, remote: InetSocketAddress, local: InetSocketAddress) extends Actor {
  val aggregator = context.actorSelection("/user/aggregator")

  aggregator ! QCandles()
  aggregator ! Reg()

  println(s"client connected: $remote $local")

  def receive = {

    case Received(data) =>

    case PeerClosed =>
      disconnect(None)

    case ErrorClosed(cause) =>
      disconnect(Some(cause))

    case ACandles(candles) =>
      connection ! Write(SpraySerialization.serialize(candles))

    case CurCandles(candles) =>
      connection ! Write(SpraySerialization.serialize(candles))

  }

  def disconnect(cause: Option[String]) {
    println(s"client disconnected")
    cause.foreach(println)
    aggregator ! Unreg()
    context.stop(self)
  }
}