package com.agoda.rest

import java.time.LocalDateTime

import akka.actor.ActorRef
import com.agoda.ConfigurationSupport.Downloader.defaultLocation
import com.agoda.actors.DownloadFile
import com.agoda.actors.DownloadFlow.{BulkDownload, BulkDownloadMode}
import com.agoda.domain.Pong
import com.agoda.dto.DownloadFileDto
import com.agoda.dto.DownloadFileJsonProtocol._
import spray.httpx.SprayJsonSupport._
import spray.routing.{HttpService, RequestContext}

trait DownloadRoute extends HttpService {

  def downloadFlow(context: RequestContext): ActorRef

  def downloadRoute = pathPrefix("api") {
    path("download") {
      post {
        entity(as[DownloadFileDto]) { dto =>
          requestContext => downloadFlow(requestContext) ! DownloadFile(dto)
        }
      }
    } ~ path("bulkdownload") {
      post {
        entity(as[List[String]]) { urlList =>
          requestContext => {
            val flowActor = downloadFlow(requestContext)
            flowActor ! BulkDownloadMode
            flowActor ! BulkDownload(urlList, defaultLocation)
          }
        }
      }
    } ~ path("ping") {
      complete(Pong(LocalDateTime.now().toString))
    }
  }
}
