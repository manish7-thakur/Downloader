package com.agoda.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import spray.routing.RequestContext

class ApiActorSpecs extends TestKit(ActorSystem("RestApiActorSpec", ConfigFactory.load("test"))) with ImplicitSender with WordSpecLike with BeforeAndAfterAll with Mockito{

  "RestApiActor" should {
    "create FlowActor for individual Requests" in new Specification {
      val rc = mock[RequestContext]
      val actor = TestActorRef[RestApiActor](Props[RestApiActor], "RestApiActor")
      val ac1 = actor.underlyingActor.downloadFlow(rc)
      val ac2 = actor.underlyingActor.downloadFlow(rc)
      ac1.compareTo(ac2) shouldNotEqual 0
    }
  }
}
