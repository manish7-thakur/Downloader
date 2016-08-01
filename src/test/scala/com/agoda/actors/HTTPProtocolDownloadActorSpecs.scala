package com.agoda.actors

import java.nio.file.{Files, Paths}
import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import com.agoda.actors.DownloadFlow.{FileDownloadFailed, FileDownloaded, InvalidDirectory}
import com.agoda.util.RandomUtil
import com.typesafe.config.ConfigFactory
import org.specs2.matcher.Scope
import org.specs2.mutable.Specification

import scala.concurrent.duration.FiniteDuration

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
    "create the file on disk with the specified name" in new Specification {
      val path = Paths.get("src/test/resources/www.google.com")
      Files.exists(path) shouldEqual true
      Files.deleteIfExists(path)
    }
    "respond if the file could not be downloaded" in new ActorScope {
      httpDownloadActor ! DownloadFile("sf://unknownhost", "src/test/resources")
      expectMsgClass(classOf[FileDownloadFailed])
    }
    "download the file with ftp protocol" in new Specification with ActorScope {
      val pathString = "src/test/resources/rfc959.txt"
      httpDownloadActor ! DownloadFile("ftp://ftp.funet.fi/pub/standards/RFC/rfc959.txt", "src/test/resources")
      expectMsg(FiniteDuration(1, TimeUnit.MINUTES), FileDownloaded(pathString))
      Files.exists(Paths.get(pathString)) shouldEqual true
      Files.deleteIfExists(Paths.get(pathString))
    }
  }
}
