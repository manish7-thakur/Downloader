package com.agoda.actors

import java.io.IOException
import java.net.UnknownHostException

import scala.concurrent.duration._

import akka.actor._
import akka.actor.SupervisorStrategy.{Restart, Stop}
import com.agoda.actors.DeleteFileFlow.DeleteFile
import com.agoda.actors.DownloadFlow._
import com.agoda.downloader.DownloadUtils
import com.agoda.util.RandomUtil
import spray.http.StatusCodes
import spray.routing.RequestContext

class DownloadFlowActor(ctx: RequestContext, deleteFileActor: ActorRef) extends FlowActor(ctx) with DownloadUtils with RandomUtil {

  override val supervisorStrategy = OneForOneStrategy(2, 5 seconds) {
    case _: UnknownHostException => Stop
    case _: IOException => Restart
    case _: Exception => Stop
  }

  def receive = {
    case DownloadFile(url, location) => {
      val protocol = getProtocol(url)
      protocol match {
        case "http" | "ftp" | "https" => createWorkerChild(Props[OpenProtocolDownloadActor], "OpenProtocolDownloadActor") ! DownloadFile(url, location)
        case "sftp" => createWorkerChild(Props[SFTProtocolDownloadActor], "SftpDownloadActor") ! DownloadFile(url, location)
        case _ => completeRequest(StatusCodes.NotFound, "Invalid Protocol: " + protocol)
      }
    }
    case InvalidDirectory(location) => completeRequest(StatusCodes.NotFound, "Directory not found : " + location)

    case FileDownloaded(path) => completeRequest(StatusCodes.OK, "File Downloaded : " + path)

    case FileDownloadFailed(path, cause: Throwable) => {
      deleteFileActor ! DeleteFile(path)
      completeRequest(StatusCodes.InternalServerError, cause.getMessage)
    }
    case BulkDownloadMode => context.become(bulkDownload)

  }

  var statusMap = scala.collection.mutable.HashMap[String, String]()

  def bulkDownload: Receive = {
    case BulkDownload(urls, location) =>   urls foreach { url =>
      val protocol = getProtocol(url)
      protocol match {
        case "http" | "ftp" | "https" => {
          createWorkerChild(Props[OpenProtocolDownloadActor], "OpenProtocolDownloadActor") ! DownloadFile(url, location)
        }
        case "sftp" => {
          createWorkerChild(Props[SFTProtocolDownloadActor], "SftpDownloadActor") ! DownloadFile(url, location)
        }
        case _ => statusMap += (url -> s"Invalid Protocol: $protocol")
      }}
    case InvalidDirectory(location) => completeRequest(StatusCodes.NotFound, "Directory not found : " + location)

    case FileDownloaded(path) => {
      statusMap += (path -> "OK")
      sender ! PoisonPill
    }
    case FileDownloadFailed(path, cause) => {
      statusMap += (path -> cause.getMessage)
      sender ! PoisonPill
    }
  }

  def createWorkerChild(props: Props, name: String) = context.actorOf(props, name + randomUUID)
}
