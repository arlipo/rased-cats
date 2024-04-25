package com.rased.server.api

import com.rased.server.api.common.ExecutionLogger
import com.rased.server.model.ClientDataDto

object ServerLogging {
  case class FingerprintCalculationLogger(clientDataDto: ClientDataDto) extends ExecutionLogger {
    def logStart: String = s"Calculating fingerprint for $deviceType"
    def log2xx: String = s"Fingerprint for $deviceType successfully calculated"
    def log4xx(reason: String): String = s"Client error on requesting fingerprint for $deviceType: $reason"
    def log5xx(ex: Throwable): String = s"Server error on calculating fingerprint for $deviceType: ${ex.getMessage}"

    def deviceType: String = clientDataDto match {
      case _: ClientDataDto.Android => "Android"
      case _: ClientDataDto.Iphone  => "iPhone"
    }
  }
}
