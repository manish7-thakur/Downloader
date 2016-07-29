package com.agoda.util

import java.util.UUID

trait RandomUtil {
  def randomUUID = UUID.randomUUID().toString
}
