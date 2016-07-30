package com.agoda.actors

import java.nio.file.{Files, Paths}

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.agoda.actors.DownloadFlow.{DownloadFile, FileDownloaded, InvalidDirectory}
import com.agoda.util.RandomUtil
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class SFTPProtocolDownloadActorSpecs extends TestKit(ActorSystem("SFTPSpec", ConfigFactory.load("test"))) with ImplicitSender with WordSpecLike with BeforeAndAfterAll with RandomUtil{
  override def afterAll() = TestKit.shutdownActorSystem(system)


  "SFTPDownloadActor" should {
    "verify if the directory exists or not" in {
      val sftpDownloadActor = system.actorOf(Props[SFTProtocolDownloadActor], "SFTPDownloadActor" + randomUUID)
      sftpDownloadActor ! DownloadFile("sftp://username:password@hostname;/filename/gfr", "/downloads")
      expectMsg(InvalidDirectory("/downloads"))
    }
    //Needs a SFTP server local to your machine
    "download the file in the mentioned directory with file name" in {
      val sftpDownloadActor = system.actorOf(Props[SFTProtocolDownloadActor], "SFTPDownloadActor" + randomUUID)
      sftpDownloadActor ! DownloadFile("sftp://mthakur:l2dwq#y4b49@192.168.1.17;/etc/hosts", "src/test/resources")
      expectMsg(FileDownloaded("hosts"))
    }
    "create the file on disk with the specified name" in {
      val path = Paths.get("src/test/resources/hosts")
      val exists = Files.exists(path)
      exists === true
     Files.deleteIfExists(path)
    }
  }
}
