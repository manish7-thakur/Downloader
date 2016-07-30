package com.agoda.actors

import java.nio.file.{Files, Paths}

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.agoda.actors.DownloadFlow.{FileDownloaded, InvalidDirectory}
import com.agoda.util.RandomUtil
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class HTTPProtocolDownloadActorSpecs extends TestKit(ActorSystem("HTTPSpec", ConfigFactory.load("test"))) with ImplicitSender with WordSpecLike with BeforeAndAfterAll with RandomUtil {
  override def afterAll() = TestKit.shutdownActorSystem(system)


  "HTTPProtocolDownloadActor" should {
    "verify if the directory exists or not" in {
      val httpDownloadActor = system.actorOf(Props[HTTPProtocolDownloadActor], "HTTPDownloadActor" + randomUUID)
      httpDownloadActor ! DownloadFile("http://www.google.com", "/downloads")
      expectMsg(InvalidDirectory("/downloads"))
    }
    }
    "download the file in the mentioned directory with file name" in {
      val httpDownloadActor = system.actorOf(Props[HTTPProtocolDownloadActor], "HTTPDownloadActor" + randomUUID)
      httpDownloadActor ! DownloadFile("http://www.google.com", "src/test/resources")
      expectMsg(FileDownloaded("www.google.com"))
    }
  "create the file on disk with the specified name" in {
    val path = Paths.get("src/test/resources/www.google.com")
    val exists = Files.exists(path)
    exists === true
    Files.deleteIfExists(path)
  }
}
