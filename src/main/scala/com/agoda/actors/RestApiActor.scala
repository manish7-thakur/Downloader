package com.agoda.actors

import akka.actor.Props
import com.agoda.rest.DownloadRoute
import com.agoda.util.RandomUtil
import spray.routing.{HttpServiceActor, RequestContext}

class RestApiActor extends HttpServiceActor with DownloadRoute with RandomUtil{
  override def receive = runRoute(downloadRoute)

  val fileDeleteActor = context.actorOf(Props[FileDeleteActor], "FileDeleteActor")

  override def downloadFlow(ctx: RequestContext) = context.actorOf(Props(classOf[DownloadFlowActor], ctx, fileDeleteActor), "FlowActor" + randomUUID)
}
