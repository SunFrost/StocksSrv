import java.net.InetSocketAddress

import akka.actor.{ActorSystem, Props}

object MainListener extends App {
  val actorSystem = ActorSystem("actorSystem")

  val listener = actorSystem.actorOf(Props[Listener])
  val client = actorSystem.actorOf(Props(classOf[Client], new InetSocketAddress("localhost", 5556), listener))

}
