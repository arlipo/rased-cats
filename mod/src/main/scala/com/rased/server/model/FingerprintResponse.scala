package com.rased.server.model

import com.rased.server.model.Attributes.Fingerprint

import java.time.Instant

case class FingerprintResponse(
  fingerprint: Fingerprint,
  prevLogin:   Option[Instant]
)
