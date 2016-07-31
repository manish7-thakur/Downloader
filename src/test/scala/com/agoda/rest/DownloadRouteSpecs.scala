package com.agoda.rest

import akka.actor.{Actor, ActorSystem, ActorRefFactory, Props}
import akka.testkit.{TestActorRef, TestProbe}
import com.agoda.actors.{DownloadFile, FlowActor}
import com.agoda.dto.DownloadFileDto
import org.specs2.mutable.Specification
import spray.http.StatusCodes
import spray.json.{JsString, JsObject}
import spray.routing.{HttpServiceActor, RequestContext}
import spray.testkit.Specs2RouteTest
import spray.httpx.SprayJsonSupport._
import com.agoda.dto.DownloadFileJsonProtocol._

import scala.concurrent.duration.DurationInt

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

class ApiActor extends HttpServiceActor with DownloadRoute {
  override def receive = runRoute(downloadRoute)

  override def downloadFlow(ctx: RequestContext) = context.actorOf(Props[FlowActor], "FlowActor")
}
