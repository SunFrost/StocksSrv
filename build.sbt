name := "StocksSrv"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.14"
libraryDependencies += "io.spray" %% "spray-json" % "1.3.4"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.14" % Test

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"