package com.agoda.Downloader

import java.io.{OutputStream, InputStream, ByteArrayOutputStream, ByteArrayInputStream}

import org.specs2.mutable.Specification

trait StreamProcessor {
  def moveBytes(in: InputStream, out: OutputStream) = Stream.continually(in.read).
    takeWhile(-1 !=).foreach(b => { out.write(b); out.flush()})
}
class StreamProcessorSpecs extends Specification with StreamProcessor {

  "StreamProcessor" should {
    "move bytes from input stream to output stream continuously" in {
      val inputStream = new ByteArrayInputStream("Will move to output stream".getBytes)
      val outputPutStream = new ByteArrayOutputStream()
      inputStream.available() shouldEqual 26
      moveBytes(inputStream, outputPutStream)
      inputStream.available() shouldEqual 0
    }
  }

}
