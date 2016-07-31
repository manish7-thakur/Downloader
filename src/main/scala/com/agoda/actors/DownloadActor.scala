package com.agoda.actors

import java.io.{BufferedInputStream, BufferedOutputStream, FileOutputStream, InputStream}
import akka.actor.Actor
import com.agoda.downloader.{Downloader, StreamProcessor}

trait DownloadActor extends Actor with StreamProcessor {
  this: StreamProcessor with Downloader =>

  def downloadFile(filePath: String, inputStream: InputStream): String = {
    val in = new BufferedInputStream(inputStream)
    val out = new BufferedOutputStream(new FileOutputStream(filePath))
    moveBytes(in, out)
    out.close()
    in.close()
    filePath
  }
}
