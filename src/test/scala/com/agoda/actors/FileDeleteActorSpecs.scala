package com.agoda.actors

import java.nio.file.{Files, Paths}

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.agoda.actors.DeleteFileFlow.{DeleteFile, FileDeleted}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class FileDeleteActorSpecs extends TestKit(ActorSystem("DeleteFileSpec", ConfigFactory.load("test"))) with ImplicitSender with WordSpecLike with BeforeAndAfterAll  {

  override def afterAll() = TestKit.shutdownActorSystem(system)
  "FileDeleteActor" should {
    "delete the file if exists" in {
      val pathString = "src/test/resources/victim"
      Files.createFile(Paths.get(pathString))
      val actor = system.actorOf(Props[FileDeleteActor], "FileDeleteActor")
      actor ! DeleteFile(pathString)
      expectMsg(FileDeleted)
    }
  }
}

