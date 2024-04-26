package com.rased.server.model

import com.rased.server.model.Attributes.{FingerprintString, LastSeen}

case class FingerprintResponse(
  fingerprint: FingerprintString,
  prevLogin:   Option[LastSeen]
)

object FingerprintResponse {

}
