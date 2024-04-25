package com.rased.server.config

import scala.concurrent.duration.{DurationInt, FiniteDuration}

case class ClientConfig(
  connectTimeout: FiniteDuration,
  readTimeout: FiniteDuration,
  idleTimeout: FiniteDuration,
  maxConcurrentRequests: Int
)

object ClientConfig {
  def create: ClientConfig = ClientConfig(
    connectTimeout = 5.seconds,
    readTimeout = 10.seconds,
    idleTimeout = 5.minutes,
    maxConcurrentRequests = 32,
  )
}
