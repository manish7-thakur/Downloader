package com.agoda.downloader

import java.nio.file.{Files, Paths}

import com.agoda.util.RandomUtil

trait Downloader extends RandomUtil {
  def getFilePath(url: String, location: String) = {
    val fileName = suggestFileName(url)
    val filePath = s"$location/$fileName"
    filePath
  }
  def getProtocol(urlString: String) = if(urlString.indexOf("://") != -1) urlString.substring(0, urlString.indexOf("://")) else ""
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
