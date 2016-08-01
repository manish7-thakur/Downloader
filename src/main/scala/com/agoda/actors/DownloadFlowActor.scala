package com.agoda.actors

import java.io.IOException
import java.net.UnknownHostException

import com.agoda.util.RandomUtil

import scala.concurrent.duration._

import akka.actor._
import akka.actor.SupervisorStrategy.{Restart, Stop}
import com.agoda.actors.DeleteFileFlow.DeleteFile
import com.agoda.actors.DownloadFlow.{FileDownloadFailed, FileDownloaded, FindChildren, InvalidDirectory}
import com.agoda.downloader.Downloader
import spray.http.StatusCodes
import spray.routing.RequestContext

class DownloadFlowActor(ctx: RequestContext, deleteFileActor: ActorRef) extends FlowActor(ctx) with Downloader with RandomUtil {

  override val supervisorStrategy = OneForOneStrategy(2, 5 seconds) {
    case _: UnknownHostException => Stop
    case _: IOException => Restart
    case _: Exception => Stop
  }

  def receive = {
    case DownloadFile(url, location) => {
      val protocol = getProtocol(url)
      protocol match {
        case "http" | "ftp" | "https" => createWorkerChild(Props[HTTPProtocolDownloadActor], "HttpDownloadActor") ! DownloadFile(url, location)
        case "sftp" => createWorkerChild(Props[SFTProtocolDownloadActor], "SftpDownloadActor") ! DownloadFile(url, location)
        case _ => completeRequest(StatusCodes.NotFound, "Invalid Protocol: " + protocol)
      }
    }
    case FindChildren => sender ! context.children.size
    case InvalidDirectory(location) => completeRequest(StatusCodes.NotFound, "Directory not found : " + location)

    case FileDownloaded(path) => completeRequest(StatusCodes.OK, "File Downloaded : " + path)

    case FileDownloadFailed(path, cause: Throwable) => {
      deleteFileActor ! DeleteFile(path)
      completeRequest(StatusCodes.InternalServerError, cause.getMessage)
    }
  }

  def createWorkerChild(props: Props, name: String) = context.actorOf(props, name + randomUUID)
}
