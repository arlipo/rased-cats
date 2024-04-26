package com.rased.server.api

import cats.syntax.option._
import com.rased.server.model._
import endpoints4s.algebra

trait ServerApi extends algebra.Endpoints with ServerRecords {
  private[api] def root(input: String): Path[Unit] =
    if (input == "" || input == "/") path
    else input.dropWhile(_ == '/').split("/").foldLeft(path)(_ / _)

  def executeFingerprint(prefix: String): Endpoint[UserDataDto, FingerprintResponse] = endpoint(
    request  = request(Post, root(prefix) / "fingerprint", jsonRequest[UserDataDto]),
    response = ok(jsonResponse[FingerprintResponse]),
    docs     = EndpointDocs().withDescription("Endpoint for saving and checking fingerprint.".some)
  )

  def executeFingerprintForAndroid(prefix: String): Endpoint[UserDataDto.Android, FingerprintResponse] = endpoint(
    request  = request(Post, root(prefix) / "fingerprint" / "android", jsonRequest[UserDataDto.Android]),
    response = ok(jsonResponse[FingerprintResponse]),
    docs     = EndpointDocs().withDescription("Endpoint for saving and checking fingerprint for Android.".some)
  )

  def executeFingerprintForIphone(prefix: String): Endpoint[UserDataDto.Iphone, FingerprintResponse] = endpoint(
    request  = request(Post, root(prefix) / "fingerprint" / "iphone", jsonRequest[UserDataDto.Iphone]),
    response = ok(jsonResponse[FingerprintResponse]),
    docs     = EndpointDocs().withDescription("Endpoint for saving and checking fingerprint for iPhone.".some)
  )
}
