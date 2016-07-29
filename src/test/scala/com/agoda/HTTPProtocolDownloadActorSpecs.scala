package com.agoda

import java.io.{BufferedInputStream, BufferedOutputStream, FileOutputStream}
import java.net.URL
import java.nio.file.{Files, Paths}

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.agoda.DownloadFlow.{DownloadFile, FileDownloaded, InvalidDirectory}
import com.agoda.Downloader.{Downloader, StreamProcessor}
import com.agoda.util.RandomUtil
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

object DownloadFlow {
case class FileDownloaded(fileName: String)
case class DownloadFile(url: String, location: String)
  case class InvalidDirectory(directory: String)

}

class HTTPProtocolDownloadActorSpecs extends TestKit(ActorSystem("HTTPSpec", ConfigFactory.load("test"))) with ImplicitSender with WordSpecLike with BeforeAndAfterAll with RandomUtil {
  override def afterAll() = {
    TestKit.shutdownActorSystem(system)
  }

  "HTTPProtocolDownloadActor" should {
    "should verify if the directory exists or not" in {
      val httpDownloadActor = system.actorOf(Props[HTTPProtocolDownloadActor], "HTTPDownloadActor" + randomUUID)
      httpDownloadActor ! DownloadFile("http://www.google.com", "/downloads")
      expectMsg(InvalidDirectory("/downloads"))
    }
    }
    "should download the file in the mentioned directory with file name" in {
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

class HTTPProtocolDownloadActor extends Actor with Downloader with StreamProcessor {
  def receive = {
    case DownloadFile(url: String, location: String) => {
      if(!verifyDirectory(location))
        sender ! InvalidDirectory(location)
      else {
        val in = new BufferedInputStream(new URL(url).openStream())
        val fileName = suggestFileName(url)
        val filePath = s"$location/$fileName"
        val out = new BufferedOutputStream(new FileOutputStream(filePath))
        moveBytes(in, out)
        out.close()
        in.close()
        sender ! FileDownloaded(fileName)
      }
    }
  }
}
