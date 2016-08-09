package com.agoda

import com.typesafe.config.ConfigFactory

object ConfigurationSupport {
  val config = ConfigFactory.load()
  object SpraySupport {
    val port = config.getInt("spray.can.server.port")
    val host = config.getString("spray.can.server.host")
  }

  object Downloader {
    val defaultLocation = config.getString("downloader.default-location")
  }

}
