package main

import akka.actor.{ActorSystem, Props}
import logic.Aggregator
import net.{Client, Server}
import utils.InetSocketAddressInst

object Main extends App {
  val actorSystem = ActorSystem("srvSystem")

  //На собеседовании раcтерялся и не ответил на вопрос про object как фабрика, без new
  //InetSocketAddressInst сделал через factory object, инициализация без new
  val con = InetSocketAddressInst("localhost", 5555)

  val aggregator = actorSystem.actorOf(Props[Aggregator], "aggregator")
  val client = actorSystem.actorOf(Props(classOf[Client], con, aggregator))
  val server = actorSystem.actorOf(Props[Server])
}
