package com.agoda.actors

import java.nio.file.{Files, Paths}

import akka.actor.Actor
import com.agoda.actors.DeleteFileFlow.{DeleteFile, FileDeleted}

class FileDeleteActor extends Actor {
   def receive = {
     case DeleteFile(path) => {
       Files.deleteIfExists(Paths.get(path))
       sender ! FileDeleted
     }
   }
 }
