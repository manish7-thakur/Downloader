package com.agoda.rest

import akka.actor.ActorRefFactory
import org.specs2.mutable.Specification
import spray.http.StatusCodes
import spray.routing._
import spray.testkit.Specs2RouteTest

trait DownloadRoute extends HttpService {
  def downloadRoute = pathPrefix("download") {
    post {
      complete(StatusCodes.OK)
    }
  }
}
class DownloadRouteSpecs extends Specification with Specs2RouteTest with DownloadRoute {

  def actorRefFactory: ActorRefFactory = system

  "/download" >> {
    "POST on /download" should {
      "return OK if the file is downloaded" in {
        Post("/download") ~> downloadRoute  ~> check {
          status === StatusCodes.OK
        }
      }
    }
  }

}
