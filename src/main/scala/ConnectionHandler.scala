import akka.actor.{Actor, ActorRef}
import akka.io.Tcp._

class ConnectionHandler(connection: ActorRef) extends Actor {
  val aggregator = context.actorSelection("/user/aggregator")

  aggregator ! QCandles()
  aggregator ! Reg()

  def receive = {

    case Received(data) =>

    case PeerClosed =>
      aggregator ! Unreg()
      context.stop(self)

    case ACandles(candles) =>
      connection ! Write(SpraySerialization.serialize(candles))

    case CurCandles(candles) =>
      connection ! Write(SpraySerialization.serialize(candles))

  }
}