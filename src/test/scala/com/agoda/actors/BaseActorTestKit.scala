package com.agoda.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class BaseActorTestKit(system: ActorSystem) extends TestKit(system) with ImplicitSender with WordSpecLike with BeforeAndAfterAll  {
  this: TestKit =>

  override def afterAll() = TestKit.shutdownActorSystem(system)
}
