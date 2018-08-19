import java.net.InetSocketAddress

import akka.actor.{ActorSystem, Props}

object Main extends App {
  val actorSystem = ActorSystem("actorSystem")

  val aggregator = actorSystem.actorOf(Props[Aggregator], "aggregator")
  val client = actorSystem.actorOf(Props(classOf[Client], new InetSocketAddress("localhost", 5555), aggregator))
  val server = actorSystem.actorOf(Props[Server])
}
