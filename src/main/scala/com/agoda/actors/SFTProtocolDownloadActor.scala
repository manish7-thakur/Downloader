package com.agoda.actors

import com.agoda.actors.DownloadFlow.{FileDownloaded, InvalidDirectory, DownloadFile}
import com.agoda.downloader.Downloader
import com.jcraft.jsch.{ChannelSftp, JSch, Session}

class SFTProtocolDownloadActor extends DownloadActor with Downloader {
  def receive = {
    case DownloadFile(url, location) => {
      if(!directoryExists(location))
      sender ! InvalidDirectory("/downloads")
      else {
        val  (username, password, hostname, path) = extractSftpParameters(url)
        val (session, sftpChannel) = getSFTPChannel(username, password, hostname)
        val fileName = downloadFile(url, location, sftpChannel.get(path))
        sftpChannel.exit()
        session.disconnect()
        sender ! FileDownloaded(fileName)
      }
    }
  }

  def getSFTPChannel(username: String, password: String, hostname: String): (Session, ChannelSftp) = {
    val session = new JSch().getSession(username, hostname, 22)
    session.setConfig("StrictHostKeyChecking", "no")
    session.setPassword(password)
    session.connect()
    val channel = session.openChannel("sftp")
    channel.connect()
    val sftpChannel = channel.asInstanceOf[ChannelSftp]
    (session, sftpChannel)
  }
}
