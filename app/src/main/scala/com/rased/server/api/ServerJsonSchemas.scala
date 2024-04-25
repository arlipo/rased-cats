package com.rased.server.api

import com.rased.server.model.Attributes._
import endpoints4s.algebra

trait ServerJsonSchemas extends algebra.JsonEntitiesFromSchemas {
  implicit def fingerprintSchema: JsonSchema[Fingerprint] =
      defaultStringJsonSchema.xmap(Fingerprint.apply)(_.value)
}
