package main

import akka.actor.{ActorSystem, Props}
import logic.Listener
import net.Client
import utils.InetSocketAddressInst

object MainListener extends App {
  val actorSystem = ActorSystem("listenerSystem")

  val listener = actorSystem.actorOf(Props[Listener])
  val client = actorSystem.actorOf(Props(classOf[Client], InetSocketAddressInst("localhost", 5556), listener))

}
