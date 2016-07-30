package com.agoda.actors

import com.agoda.dto.DownloadFileDto

object DownloadFlow {

  case class FileDownloaded(fileName: String)

  case class InvalidDirectory(directory: String)

  case object FindChildren

}
case class DownloadFile(url: String, location: String)

object DownloadFile {
  def fromDto(dto: DownloadFileDto) = DownloadFile(dto.url, dto.location)
}
