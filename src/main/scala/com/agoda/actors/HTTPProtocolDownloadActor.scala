package com.agoda.actors

import java.net.URL

import com.agoda.actors.DownloadFlow.{FileDownloaded, InvalidDirectory, DownloadFile}
import com.agoda.downloader.Downloader

class HTTPProtocolDownloadActor extends DownloadActor with Downloader {
  def receive = {
    case DownloadFile(url: String, location: String) => directoryExists(location) match {
      case false => sender ! InvalidDirectory(location)
      case true => {
        val fileName = downloadFile(url, location, new URL(url).openStream())
        sender ! FileDownloaded(fileName)
      }
    }
  }

}
