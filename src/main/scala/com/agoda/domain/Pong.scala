package com.agoda.domain

import spray.json.DefaultJsonProtocol

case class Pong(pong: String)

object Pong extends DefaultJsonProtocol {
  implicit val format = jsonFormat1(Pong.apply)
}

