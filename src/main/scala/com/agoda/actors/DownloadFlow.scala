package com.agoda.actors

object DownloadFlow {

  case class FileDownloaded(fileName: String)

  case class DownloadFile(url: String, location: String)

  case class InvalidDirectory(directory: String)

  case object FindChild

}
