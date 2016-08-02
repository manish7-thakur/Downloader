package com.agoda.actors

import com.agoda.dto.DownloadFileDto
import org.specs2.mutable.Specification
import com.agoda.actors.DownloadFile._


class DownloadFileSpecs extends Specification{

  "apply method for DownloadFile" should {
    "convert dto to domain" in {
      val dto = DownloadFileDto("url", "location")
      apply(dto) shouldEqual DownloadFile("url", "location")
    }
  }

}
