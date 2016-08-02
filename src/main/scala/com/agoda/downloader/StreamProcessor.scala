package com.agoda.downloader

import java.io.{InputStream, OutputStream}


trait StreamProcessor {
  //Streams helps for parallel read & write
  def moveBytes(in: InputStream, out: OutputStream) = Stream.continually(in.read).
    takeWhile(-1 !=).foreach(b => {
    out.write(b);
    out.flush()
  })
}
