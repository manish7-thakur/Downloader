package com.agoda.actors

/**
 * Created by mthakur on 29/07/16.
 */
object DownloadFlow {
case class FileDownloaded(fileName: String)
case class DownloadFile(url: String, location: String)
  case class InvalidDirectory(directory: String)

}
