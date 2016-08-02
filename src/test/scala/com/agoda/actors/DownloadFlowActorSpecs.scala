package com.agoda.actors

import java.io.IOException
import java.net.UnknownHostException

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor._
import akka.testkit.{TestActorRef, TestProbe}
import com.agoda.actors.DeleteFileFlow.DeleteFile
import com.agoda.actors.DownloadFlow.{FileDownloadFailed, FileDownloaded, InvalidDirectory}
import com.agoda.util.RandomUtil
import com.typesafe.config.ConfigFactory
import org.specs2.matcher.Scope
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import spray.http.StatusCodes
import spray.routing.RequestContext

class DownloadFlowActorSpecs extends BaseActorTestKit(ActorSystem("DownloadFlowActorSpec", ConfigFactory.load("test"))) with RandomUtil with Mockito {

  trait MockedScope extends Scope {
    val rc = mock[RequestContext]
    val downloadActorProbe = TestProbe()
    val fileDeleteActorProbe = TestProbe()
  }

  trait ForwardMessageScope extends MockedScope {
    val downloadFlowActor = TestActorRef(new DownloadFlowActor(rc, fileDeleteActorProbe.ref){
      override def createWorkerChild(props: Props, name: String) = downloadActorProbe.ref
    })
  }

  trait RequestContextScope extends MockedScope {
    val actor = system.actorOf(Props(classOf[DownloadFlowActor], rc, fileDeleteActorProbe.ref), "DownloadFlowActor")
    downloadActorProbe.watch(actor)
  }

  trait SupervisionScope extends Specification with MockedScope {
    val supervisor = TestActorRef[DownloadFlowActor](Props(classOf[DownloadFlowActor], rc, fileDeleteActorProbe.ref))
    val strategy = supervisor.underlyingActor.supervisorStrategy.decider
  }

  "DownloadFlowActor" should {
    "create child ftp actor & forward the message to it" in new ForwardMessageScope {
      downloadFlowActor ! DownloadFile("ftp://someServerAtAgoda.com/file", "src/test/resources")
      downloadActorProbe.expectMsg(DownloadFile("ftp://someServerAtAgoda.com/file", "src/test/resources"))
      }
    "create child https actor & forward the message to it" in new ForwardMessageScope {
      downloadFlowActor ! DownloadFile("https://someServerAtAgoda.com/file", "src/test/resources")
      downloadActorProbe.expectMsg(DownloadFile("https://someServerAtAgoda.com/file", "src/test/resources"))
    }
    "create child sftp actor & forward the message to it" in new ForwardMessageScope {
      downloadFlowActor ! DownloadFile("sftp://someServerAtAgoda.com/file", "src/test/resources")
      downloadActorProbe.expectMsg(DownloadFile("sftp://someServerAtAgoda.com/file", "src/test/resources"))
    }
    "stop the actor if host not found" in new SupervisionScope {
      strategy(new UnknownHostException) should be (Stop)
    }
    "restart the actor in case of IOException" in new SupervisionScope {
      supervisor.underlyingActor.supervisorStrategy.maxNrOfRetries shouldEqual 2
      strategy(new IOException) should be (Restart)
    }
    "stop the actor in case of UnknownException" in new SupervisionScope{
      strategy(new Exception) should be (Stop)
    }
    "stop itself when the file is downloaded" in new RequestContextScope {
      actor ! FileDownloaded("directory/file")
      there was one(rc).complete(StatusCodes.OK, "File Downloaded : " + "directory/file")
      downloadActorProbe.expectMsgClass(classOf[Terminated])
    }
    "complete request & stop itself if the directory could not be found" in new RequestContextScope {
      actor ! InvalidDirectory("directory/some")
      there was one(rc).complete(StatusCodes.NotFound,"Directory not found : " + "directory/some")
      downloadActorProbe.expectMsgClass(classOf[Terminated])
    }
    "ask the DeleteFileActor to remove partial data if file couldn't be downloaded" in new RequestContextScope {
      val exception = new scala.Exception
      actor ! FileDownloadFailed("path/to/file", exception)
      there was one(rc).complete(StatusCodes.InternalServerError, exception.getMessage)
      fileDeleteActorProbe.expectMsg(DeleteFile("path/to/file"))
    }
    "respond with error for invalid protocol" in new RequestContextScope {
      actor ! DownloadFile("stp://someServerAtAgoda.com/file", "src/test/resources")
      there was one(rc).complete(StatusCodes.NotFound,"Invalid Protocol : " + "stp")
      downloadActorProbe.expectMsgClass(classOf[Terminated])
    }
  }
}




