package com.agoda.actors

import java.io.IOException
import java.net.UnknownHostException

import scala.concurrent.duration._

import akka.actor._
import akka.actor.SupervisorStrategy.{Restart, Stop}
import com.agoda.actors.DeleteFileFlow.DeleteFile
import com.agoda.actors.DownloadFlow.{FileDownloadFailed, FileDownloaded, FindChildren, InvalidDirectory}
import com.agoda.downloader.Downloader
import spray.http.StatusCodes
import spray.routing.RequestContext

class DownloadFlowActor(ctx: RequestContext, deleteFileActor: ActorRef) extends FlowActor(ctx) with Downloader {

  override val supervisorStrategy = OneForOneStrategy(2, 5 seconds) {
    case _: UnknownHostException => Stop
    case _: IOException => Restart
    case _: Exception => Stop
  }

  def receive = {
    case DownloadFile(url, location) => {
      val protocol = getProtocol(url)
      createWorkerChild(protocol).fold(completeRequest(StatusCodes.NotFound, "Invalid Protocol: " + protocol))(_ ! DownloadFile(url, location))
    }
    case FindChildren => sender ! context.children.size
    case InvalidDirectory(location) => completeRequest(StatusCodes.NotFound, "Directory not found : " + location)

    case FileDownloaded(path) => completeRequest(StatusCodes.OK, "File Downloaded : " + path)

    case FileDownloadFailed(path, cause: Throwable) => {
      deleteFileActor ! DeleteFile(path)
      completeRequest(StatusCodes.InternalServerError, cause.getMessage)
    }
  }

  def createWorkerChild(protocol: String) = protocol match {
    case "http" | "ftp" => Some(context.actorOf(Props[HTTPProtocolDownloadActor], "HttpDownloadActor" + randomUUID))
    case "sftp" => Some(context.actorOf(Props[SFTProtocolDownloadActor], "SftpDownloadActor" + randomUUID))
    case _ => None
  }
}
