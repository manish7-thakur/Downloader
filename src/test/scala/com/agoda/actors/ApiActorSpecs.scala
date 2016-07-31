package com.agoda.actors

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import spray.routing.RequestContext

class ApiActorSpecs extends TestKit(ActorSystem("RestApiActorSpec", ConfigFactory.load("test"))) with ImplicitSender with WordSpecLike with BeforeAndAfterAll with Mockito{
  override def afterAll() = TestKit.shutdownActorSystem(system)

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
