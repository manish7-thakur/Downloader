package com.agoda.actors

import java.io.{BufferedInputStream, BufferedOutputStream, FileOutputStream, InputStream}

import akka.actor.Actor
import com.agoda.downloader.StreamProcessor

trait DownloadActor extends Actor with StreamProcessor {

  def downloadFile(filePath: String, inputStream: InputStream) = {
    //Using default buffer size of 8192
    val in = new BufferedInputStream(inputStream)
    val out = new BufferedOutputStream(new FileOutputStream(filePath))
    moveBytes(in, out)
    out.close()
    in.close()
    filePath
  }
}
