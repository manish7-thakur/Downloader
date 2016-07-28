package com.agoda.Downloader

import org.specs2.mutable.Specification

trait Downloader {
  def getProtocol(urlString: String) = urlString.split("://").headOption

}
class DownloaderSpecs extends Specification with Downloader{


  "Downloader" should {
    "return http protocol from the URL" in {
      getProtocol("http://www.google.com") === Some("http")
    }
    "return ftp protocol from the URL" in {
      getProtocol("ftp://www.file.com/file") === Some("ftp")
    }
  }

}
