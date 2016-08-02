package com.agoda.Downloader

import com.agoda.downloader.DownloadUtils
import org.specs2.mutable.Specification

class DownloadUtilsSpecs extends Specification with DownloadUtils {

  "DownloaderUtils" >> {
    "#getProtocol" should {
      "return http protocol for the URL" in {
        getProtocol("http://www.google.com") === "http"
      }
      "return ftp protocol for the URL" in {
        getProtocol("ftp://www.file.com/file") === "ftp"
      }
      "return None for Bad URL" in {
        getProtocol("gtpp:/www.file.com/file") === ""
      }
      "directoryExists" should {
        "say so if download directory doesn't exists" in {
          directoryExists("/ext/invalid") mustEqual false
        }
        "say so if download directory doesn't exists" in {
          directoryExists("/etc") mustEqual true
        }
      }
      "#extractSftpParameters" should {
        "split the url into username, host, port, filePath" in {
          extractSftpParameters("sftp://username:password@hostname;/filename/gfr") shouldEqual("username", "password", "hostname", "/filename/gfr")
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
        "should still include the domain name when path mentioned is empty " in {
          suggestFileName("http://www.google.com/") shouldEqual "www.google.com"
        }
      }
    }
  }
}
