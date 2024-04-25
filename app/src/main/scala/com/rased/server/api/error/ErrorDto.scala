package com.rased.server.api.error

import io.circe.{Codec, Encoder}
import io.circe.generic.semiauto.{deriveCodec, deriveEncoder}

case class ErrorDto(code: String, message: String, path: String)
object ErrorDto {
  implicit val errorEncoder: Codec[ErrorDto] = deriveCodec
}
