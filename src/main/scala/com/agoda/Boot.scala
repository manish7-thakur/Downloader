package com.agoda

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.agoda.actors.RestApiActor
import spray.can.Http

object Boot extends App{
  implicit val actorSystem = ActorSystem("DownloadServer")
  val restApiActor = actorSystem.actorOf(Props[RestApiActor], "RestApiActor")
  //Move to application.conf
  IO(Http) ! Http.Bind(listener = restApiActor, interface = "0.0.0.0", port = 5000)
}
