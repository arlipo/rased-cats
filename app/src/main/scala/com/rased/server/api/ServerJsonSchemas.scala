package com.rased.server.api

import com.rased.server.model.Attributes._
import endpoints4s.algebra

import java.time.{OffsetDateTime, ZoneOffset}

trait ServerJsonSchemas extends algebra.JsonEntitiesFromSchemas {
  implicit def fingerprintSchema: JsonSchema[FingerprintString] =
      defaultStringJsonSchema.xmap(FingerprintString.apply)(_.value)
  implicit def lastSeenSchema: JsonSchema[LastSeen] =
    offsetDateTimeSchema.xmap(odt => LastSeen(odt.toLocalDateTime))(ls => OffsetDateTime.of(ls.localDateTime, ZoneOffset.UTC))
}
