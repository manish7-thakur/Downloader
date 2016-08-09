package com.agoda.rest

import akka.actor.{Actor, ActorRefFactory}
import akka.testkit.TestActorRef
import com.agoda.actors.DownloadFile
import com.agoda.actors.DownloadFlow.{BulkDownload, BulkDownloadMode}
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
      case BulkDownloadMode =>
      case BulkDownload(urlList, defaultLocation) => requestContext.complete(urlList)
    }
  })

  "/download" >> {
    "POST on api/download" should {
      "unmarshall the Json to DownloadFile entity" in {
        Post("/api/download", DownloadFileDto("url", "location")) ~> downloadRoute ~> check {
          responseAs[DownloadFileDto] shouldEqual DownloadFileDto("url", "location")
        }
      }
      "reject the request if invalid data is posted" in {
        Post("/api/download", "invalid data") ~> downloadRoute ~> check {
          handled must beFalse
        }
      }
    }
  }
  "/buldownload" >> {
    "POST on api/bulkdownload" should {
      "unmarshall the json list to list of url strings" in {
        Post("/api/bulkdownload", List("url1", "url2")) ~> downloadRoute ~> check {
          handled must beTrue
          println(response.toString)
          responseAs[List[String]] shouldEqual List("url1", "url2")
        }
      }
    }
  }
}
