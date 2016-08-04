package com.agoda.actors

import com.agoda.dto.DownloadFileDto

object DownloadFlow {

  case class FileDownloaded(path: String)

  case class FileDownloadFailed(path: String, cause: Throwable)

  case class InvalidDirectory(directory: String)
  case class BulkDownload(strings: Seq[String], defaultLocation: String)

}

case class DownloadFile(url: String, location: String)

object DownloadFile {
  def apply(dto: DownloadFileDto): DownloadFile = DownloadFile(dto.url, dto.location)
}

object DeleteFileFlow {

  case class DeleteFile(path: String)

}
