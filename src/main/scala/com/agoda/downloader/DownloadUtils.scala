package com.agoda.downloader

import java.nio.file.{Files, Paths}

trait DownloadUtils {
  def getFilePath(url: String, location: String) = {
    val fileName = suggestFileName(url)
    val filePath = s"$location/$fileName"
    filePath
  }

  def getProtocol(urlString: String) = if (urlString.indexOf("://") != -1) urlString.substring(0, urlString.indexOf("://")) else ""

  def directoryExists(directory: String) = Files.exists(Paths.get(directory))

  def extractSftpParameters(s: String) = {
    val protocolAndTail = s.split("://")
    val usernameAndTail = protocolAndTail.last.split(":")
    val username = usernameAndTail.head
    val passwordAndTail = usernameAndTail.last.split("@")
    val password = passwordAndTail.head
    val hostnameAndTail = passwordAndTail.last.split(";")
    val hostname = hostnameAndTail.head
    val path = hostnameAndTail.last
    (username, password, hostname, path)
  }

  def suggestFileName(url: String) = url.substring(url.lastIndexOf("/") + 1)
}
