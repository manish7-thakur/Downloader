package com.agoda

import com.typesafe.config.ConfigFactory
import org.specs2.mutable.Specification


class ConfigurationSpecs extends Specification{

  "Configuration" should {
    "be able to load the settings from configuration file" in {
      val config = ConfigFactory.load()
      config.getString("spray.can.server.request-timeout") shouldEqual "infinite"
    }
  }

}
