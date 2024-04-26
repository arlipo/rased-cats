package com.rased.server.model

import java.time.LocalDateTime

object Attributes {
  case class FingerprintString(value: String) extends AnyVal
  case class Fingerprint(value: Long)         extends AnyVal
  case class LastSeen(localDateTime: LocalDateTime) extends AnyVal
}
