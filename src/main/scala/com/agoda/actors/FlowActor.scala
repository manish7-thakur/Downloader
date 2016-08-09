package com.agoda.actors

import akka.actor.Actor
import spray.http.{StatusCode, StatusCodes}
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

abstract class FlowActor(ctx: RequestContext) extends Actor {
  this: Actor =>

  def completeRequest(statusCode: StatusCode, message: String): Unit = {
    ctx.complete(statusCode, message)
    context.stop(self)
  }

  def completeRequest(statusCode: StatusCode = StatusCodes.OK, statusMap: Map[String, String]) = {
    ctx.complete(statusCode, statusMap)
    context.stop(self)
  }

}
