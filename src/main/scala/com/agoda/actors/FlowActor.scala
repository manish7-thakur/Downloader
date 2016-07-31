package com.agoda.actors

import akka.actor.Actor
import spray.http.{StatusCode, StatusCodes}
import spray.routing.RequestContext

abstract class FlowActor(ctx: RequestContext) extends Actor {
  this: Actor =>

  def completeRequest(statusCode: StatusCode = StatusCodes.NotFound, message: String): Unit = {
    ctx.complete(statusCode, message)
    context.stop(self)
  }
}
