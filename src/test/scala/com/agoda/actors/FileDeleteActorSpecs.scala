package com.agoda.actors

import java.nio.file.{Files, Paths}

import akka.actor.{ActorSystem, Props}
import com.agoda.actors.DeleteFileFlow.{DeleteFile, FileDeleted}
import com.typesafe.config.ConfigFactory

class FileDeleteActorSpecs extends BaseActorTestKit(ActorSystem("DeleteFileSpec", ConfigFactory.load("test"))) {

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

