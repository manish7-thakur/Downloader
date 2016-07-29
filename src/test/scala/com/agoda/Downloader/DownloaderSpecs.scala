package com.agoda.Downloader

import java.nio.file.{Files, Paths}

import org.specs2.mutable.Specification

trait Downloader {
  def getProtocol(urlString: String) = urlString.split("://").headOption
  def verifyDirectory(directory: String) = Files.exists(Paths.get(directory))
  def extractSftpParameters(s: String) = {
    val protocol = s.split("://").head
    val username = s.split("://").last.split(":").head
    val password = s.split("://").last.split(":").last.split("@").head
    val hostname = s.split("://").last.split(":").last.split("@").last.split(";").head
    val fileName = s.split("://").last.split(":").last.split("@").last.split(";").last
    (username, password, hostname, fileName)
  }
}
class DownloaderSpecs extends Specification with Downloader {



  "Downloader" >> {
  "#getProtocol" should {
    "return http protocol for the URL" in {
      getProtocol("http://www.google.com") === Some("http")
    }
    "return ftp protocol for the URL" in {
      getProtocol("ftp://www.file.com/file") === Some("ftp")
    }
    "verifyDirectory" should {
      "say so if download directory doesn't exists" in {
        verifyDirectory("/ext/invalid") mustEqual false
      }
      "say so if download directory doesn't exists" in {
        verifyDirectory("/etc") mustEqual true
      }
    }
    "#extractSftpParameters" should {
      "split the url into username, host, port, filePath" in {
        extractSftpParameters("sftp://username:password@hostname;/filename/gfr") shouldEqual ("username", "password", "hostname", "/filename/gfr")
      }
    }
  }
}
}
