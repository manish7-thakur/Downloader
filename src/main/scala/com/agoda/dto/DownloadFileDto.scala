package com.agoda.dto

import spray.json.DefaultJsonProtocol

case class DownloadFileDto(url: String, location: String)

object DownloadFileJsonProtocol extends DefaultJsonProtocol {
  implicit val jsonFormat = jsonFormat2(DownloadFileDto)
}




