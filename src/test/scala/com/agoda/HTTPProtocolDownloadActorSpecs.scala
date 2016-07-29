package com.agoda

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.agoda.DownloadFlow.{DownloadFile, FileDownloaded}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

object DownloadFlow {
case object FileDownloaded
case class DownloadFile(url: String, location: String)
}

class HTTPProtocolDownloadActorSpecs extends TestKit(ActorSystem("HTTPSpec", ConfigFactory.load("test"))) with ImplicitSender with WordSpecLike with BeforeAndAfterAll {
  override def afterAll() = {
    TestKit.shutdownActorSystem(system)
  }

  "HTTPProtocolDownloadActor" should {
    "should download the file in the mentioned directory" in {
      val httpDownloadActor = system.actorOf(Props[HTTPProtocolDownloadActor], "HTTPDownloadActor")
      httpDownloadActor ! DownloadFile("http://www.google.com", "/downloads")
      expectMsg(FileDownloaded)
    }
  }
}

class HTTPProtocolDownloadActor extends Actor {
  def receive = {
    case DownloadFile(url: String, location: String) => {
      sender ! FileDownloaded
    }
  }
}
