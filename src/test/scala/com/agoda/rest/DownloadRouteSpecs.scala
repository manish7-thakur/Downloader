package com.agoda.rest

import akka.actor.{Actor, ActorRefFactory}
import akka.testkit.TestActorRef
import com.agoda.actors.DownloadFile
import com.agoda.dto.DownloadFileDto
import com.agoda.dto.DownloadFileJsonProtocol._
import org.specs2.mutable.Specification
import spray.httpx.SprayJsonSupport._
import spray.routing.RequestContext
import spray.testkit.Specs2RouteTest

class DownloadRouteSpecs extends Specification with Specs2RouteTest with DownloadRoute {

  def actorRefFactory: ActorRefFactory = system

  override def downloadFlow(requestContext: RequestContext) = TestActorRef(new Actor {
    def receive = {
      case DownloadFile(url, location) => requestContext.complete(DownloadFileDto(url, location))
    }
  })

  "/download" >> {
    "POST on api/download" should {
      "unmarshall the Json to DownloadFile entity" in {
        Post("/api/download", DownloadFileDto("url", "location")) ~> downloadRoute ~> check {
          responseAs[DownloadFileDto] === DownloadFileDto("url", "location")
        }
      }
      "reject the request if invalid data is posted" in {
        Post("/api/download", "invalid data") ~> downloadRoute ~> check {
          handled must beFalse
        }
      }
    }
  }
}
