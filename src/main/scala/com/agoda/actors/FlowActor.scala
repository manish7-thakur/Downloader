package com.agoda.actors

import java.io.IOException
import java.net.UnknownHostException

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{Props, PoisonPill, OneForOneStrategy, Actor}
import com.agoda.actors.DownloadFlow.{InvalidDirectory, FindChildren}
import com.agoda.downloader.Downloader
import scala.concurrent.duration._

object FlowActor extends FlowActor

class FlowActor extends Actor with Downloader {

  override val supervisorStrategy = OneForOneStrategy(2, 5 seconds){
    case _: UnknownHostException => Stop
    case _: IOException => Restart
    case _: Exception => Stop
  }
  def receive = {
    case DownloadFile(url, location) => createWorkerChild(getProtocol(url)) ! DownloadFile(url, location)
    case FindChildren => sender ! context.children.size
    case InvalidDirectory(location) => sender ! PoisonPill
  }
  def createWorkerChild(protocol: String) = protocol match {
    case "http" | "ftp" => context.actorOf(Props[HTTPProtocolDownloadActor], "HttpDownloadActor" + randomUUID)
    case "sftp" => context.actorOf(Props[SFTProtocolDownloadActor], "SftpDownloadActor" + randomUUID)
  }
}
