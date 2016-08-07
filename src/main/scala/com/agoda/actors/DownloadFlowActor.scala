package com.agoda.actors

import java.io.IOException
import java.net.UnknownHostException

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor._
import com.agoda.actors.DeleteFileFlow.DeleteFile
import com.agoda.actors.DownloadFlow._
import com.agoda.downloader.DownloadUtils
import com.agoda.util.RandomUtil
import spray.http.StatusCodes
import spray.routing.RequestContext

import scala.concurrent.duration._

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
        case _ => completeRequest(StatusCodes.NotFound, s"Invalid Protocol: $protocol")
      }
    }
    case InvalidDirectory(location) => completeRequest(StatusCodes.NotFound, s"Directory not found : $location")

    case FileDownloaded(path) => completeRequest(StatusCodes.OK, s"File Downloaded : $path")

    case FileDownloadFailed(path, cause: Throwable) => {
      deleteFileActor ! DeleteFile(path)
      completeRequest(StatusCodes.InternalServerError, cause.getMessage)
    }
    case BulkDownloadMode => context.become(bulkDownload)

  }

  var statusMap = Map[String, String]()

  def bulkDownload: Receive = {
    case BulkDownload(urls, location) => urls foreach { url =>
      val protocol = getProtocol(url)
      protocol match {
        case "http" | "ftp" | "https" => {
          val worker = createWorkerChild(Props[OpenProtocolDownloadActor], "OpenProtocolDownloadActor")
          context.watch(worker)
          worker ! DownloadFile(url, location)
        }
        case "sftp" => {
          val worker = createWorkerChild(Props[SFTProtocolDownloadActor], "SftpDownloadActor")
          context.watch(worker)
          worker ! DownloadFile(url, location)
        }
        case _ => statusMap = statusMap + (url -> s"Invalid Protocol: $protocol")
      }
    }
    case InvalidDirectory(location) => completeRequest(StatusCodes.NotFound, s"Directory not found : $location")

    case FileDownloaded(path) => {
      statusMap = statusMap + (path -> "OK")
      sender ! PoisonPill
    }
    case FileDownloadFailed(path, cause) => {
      statusMap = statusMap + (path -> cause.getMessage)
      sender ! PoisonPill
    }
    case Terminated(actor) => if (context.children.size == 0) completeRequest(StatusCodes.OK, statusMap)
  }

  def createWorkerChild(props: Props, name: String) = context.actorOf(props, name + randomUUID)
}
