package com.agoda.actors

import java.net.URL

import com.agoda.actors.DownloadFlow.{FileDownloadFailed, FileDownloaded, InvalidDirectory}
import com.agoda.downloader.Downloader

import scala.util.{Failure, Success, Try}

class HTTPProtocolDownloadActor extends DownloadActor with Downloader {
  def receive = {
    case DownloadFile(url: String, location: String) => directoryExists(location) match {
      case false => sender ! InvalidDirectory(location)
      case true => {
        val filePath = getFilePath(url, location)
        Try(downloadFile(filePath, new URL(url).openStream())) match {
          case Success(value) => sender ! FileDownloaded(filePath)
          case Failure(ex) => sender ! FileDownloadFailed(filePath, ex)
        }
      }
    }
  }

}
