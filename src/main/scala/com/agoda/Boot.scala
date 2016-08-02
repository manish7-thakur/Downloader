package com.agoda

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.agoda.actors.RestApiActor
import com.typesafe.config.ConfigFactory
import spray.can.Http
import com.agoda.ConfigurationSupport.SpraySupport._

object Boot extends App{
  implicit val actorSystem = ActorSystem("DownloadServer", ConfigFactory.load)
  val restApiActor = actorSystem.actorOf(Props[RestApiActor], "RestApiActor")
  IO(Http) ! Http.Bind(listener = restApiActor, interface = host, port = port)
}
