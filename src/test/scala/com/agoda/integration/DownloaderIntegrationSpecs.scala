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

class DownloaderIntegrationSpecs extends Specification {

  trait IntegrationScope extends Scope {
    val waitAtMost10Sec = FiniteDuration(10, TimeUnit.SECONDS)

    def hitNow(req: Req, waitAtMost: FiniteDuration): Response = {
      result(Http(req > (r => r)), waitAtMost)
    }
  }

  "api/download" should {
    "return OK after downloading the file in mentioned directory" in new IntegrationScope {
      private val baseUrl = "api/download"
      val req = url(s"http://$host:$port/$baseUrl").setContentType("application/json", "UTF-8") << """{"url": "http://www.google.com", "location": "src/test/resources"}""" POST
      val response = hitNow(req, waitAtMost10Sec)
      val pathString = "src/test/resources/www.google.com"
      response.getResponseBody shouldEqual "File Downloaded : " + pathString
      Files.exists(Paths.get(pathString)) shouldEqual true
      Files.deleteIfExists(Paths.get(pathString))
    }
    "return error if download directory could not be found" in new IntegrationScope {
      val req: Req = url("http://localhost:5000/api/download").setContentType("application/json", "UTF-8") << """{"url": "http://www.google.com", "location": "src/invalid/directory"}""" POST
      val response = hitNow(req, waitAtMost10Sec)
      response.getResponseBody shouldEqual "Directory not found : src/invalid/directory"
    }
    "return error if protocol is not valid" in new IntegrationScope {
      val req: Req = url("http://localhost:5000/api/download").setContentType("application/json", "UTF-8") << """{"url": "Boom://www.google.com", "location": "src/test/resources"}""" POST
      val response = hitNow(req, waitAtMost10Sec)
      response.getResponseBody shouldEqual "Invalid Protocol: Boom"
    }
  }

}
