package com.agoda.actors

import java.io.IOException
import java.net.UnknownHostException

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor._
import com.agoda.actors.DeleteFileFlow.DeleteFile
import com.agoda.actors.DownloadFlow.{FileDownloadFailed, FileDownloaded, InvalidDirectory, FindChildren}
import com.agoda.downloader.Downloader
import spray.http.StatusCodes
import spray.routing.RequestContext
import scala.concurrent.duration._

class FlowActor(ctx: RequestContext, deleteFileActor: ActorRef) extends Actor with Downloader {

  override val supervisorStrategy = OneForOneStrategy(2, 5 seconds){
    case _: UnknownHostException => Stop
    case _: IOException => Restart
    case _: Exception => Stop
  }
  def receive = {
    case DownloadFile(url, location) => createWorkerChild(getProtocol(url)) ! DownloadFile(url, location)
    case FindChildren => sender ! context.children.size
    case InvalidDirectory(location) => {
      ctx.complete(StatusCodes.NotFound, "Directory not found : " + location)
      context.stop(self)
    }
    case FileDownloaded(path) => {
      ctx.complete("File Downloaded : " + path)
      context.stop(self)
    }
    case FileDownloadFailed(path, cause: Throwable) => {
      deleteFileActor ! DeleteFile(path)
      ctx.complete(StatusCodes.InternalServerError, cause.getMessage)
      context.stop(self)
    }
  }
  def createWorkerChild(protocol: String) = protocol match {
    case "http" | "ftp" => context.actorOf(Props[HTTPProtocolDownloadActor], "HttpDownloadActor" + randomUUID)
    case "sftp" => context.actorOf(Props[SFTProtocolDownloadActor], "SftpDownloadActor" + randomUUID)
  }
}
