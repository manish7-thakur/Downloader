package com.agoda.downloader

import java.io.{InputStream, OutputStream}

/**
 * Created by mthakur on 29/07/16.
 */
trait StreamProcessor {
  def moveBytes(in: InputStream, out: OutputStream) = Stream.continually(in.read).
    takeWhile(-1 !=).foreach(b => { out.write(b); out.flush()})
}
