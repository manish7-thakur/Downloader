package com.agoda.actors

import java.nio.file.{Files, Paths}

import akka.actor.{ActorSystem, Props}
import com.agoda.actors.DownloadFlow.{FileDownloadFailed, FileDownloaded, InvalidDirectory}
import com.agoda.util.RandomUtil
import com.typesafe.config.ConfigFactory
import org.specs2.matcher.Scope

class HTTPProtocolDownloadActorSpecs extends BaseActorTestKit(ActorSystem("HTTPSpec", ConfigFactory.load("test"))) with RandomUtil {

  trait ActorScope extends Scope {
    val httpDownloadActor = system.actorOf(Props[HTTPProtocolDownloadActor], "HTTPDownloadActor" + randomUUID)
  }

  "HTTPProtocolDownloadActor" should {
    "verify if the directory exists or not" in new ActorScope {
      httpDownloadActor ! DownloadFile("http://www.google.com", "/downloads")
      expectMsg(InvalidDirectory("/downloads"))
    }
    "download the file in the mentioned directory with file name" in new ActorScope{
      httpDownloadActor ! DownloadFile("http://www.google.com", "src/test/resources")
      expectMsg(FileDownloaded("src/test/resources/www.google.com"))
    }
    "create the file on disk with the specified name" in {
      val path = Paths.get("src/test/resources/www.google.com")
      val exists = Files.exists(path)
      exists === true
      Files.deleteIfExists(path)
    }
    "respond if the file could not be downloaded" in new ActorScope {
      httpDownloadActor ! DownloadFile("sf://unknownhost", "src/test/resources")
      expectMsgClass(classOf[FileDownloadFailed])
    }
  }
}
