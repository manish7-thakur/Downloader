package com.agoda.actors

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.agoda.actors.DownloadFlow.{DownloadFile, FindChild}
import com.agoda.downloader.Downloader
import com.agoda.util.RandomUtil
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class FlowActorSpecs extends TestKit(ActorSystem("FlowActorSpec", ConfigFactory.load("test"))) with ImplicitSender with WordSpecLike with BeforeAndAfterAll with RandomUtil{

  override def afterAll() = TestKit.shutdownActorSystem(system)

  val flowActor = system.actorOf(Props[FlowActor], "FlowActor" + randomUUID)

  "FlowActor" should {
    "create appropriate child actor based on ftp protocol & forward the message to it" in {
      flowActor ! DownloadFile("ftp://someServerAtAgoda.com/file", "/someValidLocation")
      flowActor ! FindChild
      expectMsg(1)
    }
    "create appropriate child actor based on sftp protocol & forward the message to it" in {
      flowActor ! DownloadFile("sftp://someServerAtAgoda.com/file", "/someValidLocation")
      flowActor ! FindChild
      expectMsg(2)
    }
  }
}

class FlowActor extends Actor with Downloader {
  def receive = {
    case DownloadFile(url, location) => getProtocol(url) match {
    case Some("http") | Some("ftp") =>
      val downloadActor = context.actorOf(Props[HTTPProtocolDownloadActor], "HttpDownloadActor" + randomUUID)
      downloadActor ! DownloadFile(url, location)
    case Some("sftp") => context.actorOf(Props[SFTProtocolDownloadActor], "SftpDownloadActor" + randomUUID)
  }
    case FindChild => sender ! context.children.size
  }
}
