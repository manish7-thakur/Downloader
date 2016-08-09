package com.agoda.integration

import java.nio.file._
import java.util.concurrent.TimeUnit
import scala.concurrent.Await._
import scala.concurrent.duration.FiniteDuration
import com.ning.http.client.Response
import dispatch.{Http, Req, url}
import dispatch.Defaults._
import org.specs2.matcher.Scope
import org.specs2.mutable.Specification
import com.agoda.ConfigurationSupport.SpraySupport._
import spray.json._
import DefaultJsonProtocol._

class DownloaderIntegrationSpecs extends Specification {


  trait IntegrationScope extends Scope {
    val contentType = "application/json"
    val encoding = "UTF-8"
    val waitAtMost10Sec = FiniteDuration(10, TimeUnit.SECONDS)
    val waitAMinute = FiniteDuration(1, TimeUnit.MINUTES)
    val baseUrlSingleDownload = "api/download"
    val baseUrlBulkDownload = "api/bulkdownload"
    def hitNow(req: Req, waitAtMost: FiniteDuration): Response = {
      result(Http(req > (r => r)), waitAtMost)
    }
  }

  "api/download" should {
    "return OK after downloading the file in mentioned directory" in new IntegrationScope {
      val req = url(s"http://$host:$port/$baseUrlSingleDownload").setContentType(contentType, encoding) << """{"url": "http://www.google.com", "location": "src/test/resources"}""" POST
      val response = hitNow(req, waitAtMost10Sec)
      val pathString = "src/test/resources/www.google.com"
      response.getResponseBody shouldEqual "File Downloaded : " + pathString
      Files.exists(Paths.get(pathString)) shouldEqual true
      Files.deleteIfExists(Paths.get(pathString))
    }
    "return error if download directory could not be found" in new IntegrationScope {
      val req: Req = url(s"http://$host:$port/$baseUrlSingleDownload").setContentType(contentType, encoding) << """{"url": "http://www.google.com", "location": "src/invalid/directory"}""" POST
      val response = hitNow(req, waitAtMost10Sec)
      response.getResponseBody shouldEqual "Directory not found : src/invalid/directory"
    }
    "return error if protocol is not valid" in new IntegrationScope {
      val req: Req = url(s"http://$host:$port/$baseUrlSingleDownload").setContentType(contentType, encoding) << """{"url": "Boom://www.google.com", "location": "src/test/resources"}""" POST
      val response = hitNow(req, waitAtMost10Sec)
      response.getResponseBody shouldEqual "Invalid Protocol: Boom"
    }
  }
  "api/bulkdownload" should {
    "download multiple files simultaneously" in new IntegrationScope {
      val req = url(s"http://$host:$port/$baseUrlBulkDownload").setContentType(contentType, encoding) << """["http://www.pdf995.com/samples/widgets.pdf", "ftp://ftp.funet.fi/pub/standards/RFC/rfc959.txt"]""" POST
      val response = hitNow(req, waitAMinute)
      val firstFilePath = "src/test/resources/widgets.pdf"
      val secondFilePath = "src/test/resources/rfc959.txt"
      response.getResponseBody shouldEqual Map(firstFilePath -> "OK", secondFilePath -> "OK").toJson.prettyPrint
      Files.exists(Paths.get(firstFilePath)) shouldEqual true
      Files.exists(Paths.get(secondFilePath)) shouldEqual true
      Files.delete(Paths.get(firstFilePath))
      Files.delete(Paths.get(secondFilePath))
    }
  }
}
