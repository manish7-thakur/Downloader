package com.agoda.actors

import java.net.URL

import com.agoda.actors.DownloadFlow.{FileDownloadFailed, FileDownloaded, InvalidDirectory}
import com.agoda.downloader.DownloadUtils

import scala.util.{Failure, Success, Try}

class OpenProtocolDownloadActor extends DownloadActor with DownloadUtils {
  def receive = {
    case DownloadFile(url: String, location: String) => directoryExists(location) match {
      case false => sender ! InvalidDirectory(location)
      case true => {
        val filePath = getFilePath(url, location)
        Try {
          val connection = new URL(url).openConnection()
          //For slow servers to avoid blocking indefinitely
          connection.setReadTimeout(10000)
          downloadFile(filePath, connection.getInputStream)
        } match {
          case Success(value) => sender ! FileDownloaded(filePath)
          case Failure(ex) => sender ! FileDownloadFailed(filePath, ex)
        }
      }
    }
  }

}
