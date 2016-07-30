package com.agoda.actors

import java.io.IOException
import java.net.UnknownHostException

import akka.actor._
import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.agoda.util.RandomUtil
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import org.specs2.matcher.Scope
import org.specs2.mutable.Specification

class FlowActorSpecs extends TestKit(ActorSystem("FlowActorSpec", ConfigFactory.load("test"))) with ImplicitSender with WordSpecLike with BeforeAndAfterAll with RandomUtil {

  override def afterAll() = TestKit.shutdownActorSystem(system)

  trait ForwardMessageScope extends Scope {
    val probe = TestProbe()
    val flowActor = TestActorRef(new FlowActor{
      override def createWorkerChild(protocol: String) = probe.ref
    })
  }

  "FlowActor" should {
    "create child ftp actor & forward the message to it" in new ForwardMessageScope {
      flowActor ! DownloadFile("ftp://someServerAtAgoda.com/file", "src/test/resources")
      probe.expectMsg(DownloadFile("ftp://someServerAtAgoda.com/file", "src/test/resources"))
      }
    "create child sftp actor & forward the message to it" in new ForwardMessageScope {
      flowActor ! DownloadFile("sftp://someServerAtAgoda.com/file", "src/test/resources")
      probe.expectMsg(DownloadFile("sftp://someServerAtAgoda.com/file", "src/test/resources"))
    }
    "stop the actor if host not found" in new Specification {
      val supervisor = TestActorRef[FlowActor](Props[FlowActor])
      val strategy = supervisor.underlyingActor.supervisorStrategy.decider
      strategy(new UnknownHostException) should be (Stop)
    }
    "restart the actor in case of IOException" in new Specification {
      val supervisor = TestActorRef[FlowActor](Props[FlowActor])
      val strategy = supervisor.underlyingActor.supervisorStrategy.decider
      supervisor.underlyingActor.supervisorStrategy.maxNrOfRetries shouldEqual 2
      strategy(new IOException) should be (Restart)
    }
    "stop the actor in case of UnknownException" in new Specification {
      val supervisor = TestActorRef[FlowActor](Props[FlowActor])
      val strategy = supervisor.underlyingActor.supervisorStrategy.decider
      strategy(new Exception) should be (Stop)
    }
  }
}




