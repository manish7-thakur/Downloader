package com.agoda.actors

import java.nio.file.{Files, Paths}

import akka.actor.{ActorSystem, Props}
import com.agoda.actors.DeleteFileFlow.DeleteFile
import com.typesafe.config.ConfigFactory
import org.specs2.mutable.Specification

class FileDeleteActorSpecs extends BaseActorTestKit(ActorSystem("DeleteFileSpec", ConfigFactory.load("test"))) {

  "FileDeleteActor" should {
    "delete the file if exists" in new Specification {
      val pathString = "src/test/resources/victim"
      Files.createFile(Paths.get(pathString))
      val actor = system.actorOf(Props[FileDeleteActor], "FileDeleteActor")
      actor ! DeleteFile(pathString)
      Files.exists(Paths.get(pathString)) shouldEqual false
    }
  }
}

