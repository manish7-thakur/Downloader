package com.agoda.rest

import akka.actor.ActorRef
import com.agoda.actors.DownloadFile
import com.agoda.dto.DownloadFileDto
import com.agoda.dto.DownloadFileJsonProtocol._
import spray.httpx.SprayJsonSupport._
import spray.routing.{HttpService, RequestContext}

trait DownloadRoute extends HttpService {

  def downloadFlow(context: RequestContext): ActorRef

  def downloadRoute = pathPrefix("api" / "download") {
    post {
      entity(as[DownloadFileDto]) { dto =>
        requestContext => downloadFlow(requestContext) ! DownloadFile(dto)
      }
    }
  }
}
