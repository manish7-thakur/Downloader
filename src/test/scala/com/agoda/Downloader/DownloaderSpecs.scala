package com.agoda.Downloader

import java.nio.file.{Files, Paths}

import com.agoda.util.RandomUtil
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

trait Downloader extends RandomUtil {
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
  def suggestFileName(url: String) = url.substring(url.lastIndexOf("/") + 1)
}
class DownloaderSpecs extends Specification with Downloader with Mockito {



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
    "#suggestFileName" should {
      "uniquely determine file name from the URL" in {
        suggestFileName("http://my.file.com/file") shouldEqual "file"
      }
      "should include the extension name" in {
        suggestFileName("sftp://and.also.this/ending.end") shouldEqual "ending.end"
      }
      "should include the domain name when path is not mentioned" in {
        suggestFileName("http://www.google.com") shouldEqual "www.google.com"
      }
    }
  }
}
}
