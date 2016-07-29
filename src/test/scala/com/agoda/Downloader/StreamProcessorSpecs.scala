package com.agoda.Downloader

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import com.agoda.downloader.StreamProcessor
import org.specs2.mutable.Specification

class StreamProcessorSpecs extends Specification with StreamProcessor {

  "StreamProcessor" should {
    "move bytes from input stream to output stream continuously" in {
      val inputStream = new ByteArrayInputStream("Will move to output stream".getBytes)
      val outputPutStream = new ByteArrayOutputStream()
      inputStream.available() shouldEqual 26
      moveBytes(inputStream, outputPutStream)
      inputStream.available() shouldEqual 0
      outputPutStream.size() shouldEqual 26
    }
  }

}
