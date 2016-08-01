package com.agoda.actors

import com.agoda.actors.DownloadFlow.{FileDownloadFailed, FileDownloaded, InvalidDirectory}
import com.agoda.downloader.Downloader
import com.jcraft.jsch.{ChannelSftp, JSch, Session}

import scala.util.{Failure, Success, Try}

class SFTProtocolDownloadActor extends DownloadActor with Downloader {
  def receive = {
    case DownloadFile(url, location) => directoryExists(location) match {
      case false => sender ! InvalidDirectory(location)
      case true => {
        val (username, password, hostname, path) = extractSftpParameters(url)
        val filePath = getFilePath(url, location)
        Try {
          val (session, sftpChannel) = getSFTPChannel(username, password, hostname)
          downloadFileAndCloseChannel(path, filePath, session, sftpChannel)
        } match {
          case Success(value) => sender ! FileDownloaded(filePath)
          case Failure(ex) => sender ! FileDownloadFailed(filePath, ex)
        }
      }
    }
  }

  def downloadFileAndCloseChannel(path: String, filePath: String, session: Session, sftpChannel: ChannelSftp): Unit = {
    downloadFile(filePath, sftpChannel.get(path))
    sftpChannel.exit()
    session.disconnect()
  }

  def getSFTPChannel(username: String, password: String, hostname: String): (Session, ChannelSftp) = {
    val session = new JSch().getSession(username, hostname, 22)
    session.setConfig("StrictHostKeyChecking", "no")
    session.setPassword(password)
    session.connect()
    session.setTimeout(10000)
    val channel = session.openChannel("sftp")
    channel.connect()
    val sftpChannel = channel.asInstanceOf[ChannelSftp]
    (session, sftpChannel)
  }
}
