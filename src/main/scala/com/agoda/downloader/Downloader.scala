package com.agoda.downloader

import java.nio.file.{Files, Paths}

import com.agoda.util.RandomUtil

/**
 * Created by mthakur on 29/07/16.
 */
trait Downloader extends RandomUtil {
  def getProtocol(urlString: String) = urlString.split("://").headOption
  def directoryExists(directory: String) = Files.exists(Paths.get(directory))
  def extractSftpParameters(s: String) = {
    val protocol = s.split("://").head
    val username = s.split("://").last.split(":").head
    val password = s.split("://").last.split(":").last.split("@").head
    val hostname = s.split("://").last.split(":").last.split("@").last.split(";").head
    val path = s.split("://").last.split(":").last.split("@").last.split(";").last
    (username, password, hostname, path)
  }
  def suggestFileName(url: String) = url.substring(url.lastIndexOf("/") + 1)
}
