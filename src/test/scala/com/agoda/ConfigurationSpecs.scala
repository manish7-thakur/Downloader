package com.agoda

import com.typesafe.config.ConfigFactory
import org.specs2.mutable.Specification

class ConfigurationSpecs extends Specification {

  "ConfigurationSupport" should {
    "be able to load the settings from configuration file" in {
      val config = ConfigFactory.load()
      config.getString("spray.can.server.request-timeout") shouldEqual "infinite"
    }
    "be able to read the default-location from Configuration file" in {
      ConfigurationSupport.Downloader.defaultLocation shouldEqual "/Users/mthakur/downloads"
    }
    "be able to read the port settings from Configuration file" in {
      ConfigurationSupport.SpraySupport.port shouldEqual 5000
      ConfigurationSupport.SpraySupport.host shouldEqual "0.0.0.0"
    }
  }
}
