package com.rased.server.api

import cats.syntax.option._
import com.rased.server.model._
import endpoints4s.algebra

trait ServerApi extends algebra.Endpoints with ServerRecords {
  private[api] def root(input: String): Path[Unit] =
    if (input == "" || input == "/") path
    else input.dropWhile(_ == '/').split("/").foldLeft(path)(_ / _)

  def executeFingerprint(prefix: String): Endpoint[ClientDataDto, FingerprintResponse] = endpoint(
    request  = request(Post, root(prefix) / "fingerprint", jsonRequest[ClientDataDto]),
    response = ok(jsonResponse[FingerprintResponse]),
    docs     = EndpointDocs().withDescription("Endpoint for saving and checking fingerprint.".some)
  )

  def executeFingerprintForAndroid(prefix: String): Endpoint[ClientDataDto.Android, FingerprintResponse] = endpoint(
    request  = request(Post, root(prefix) / "fingerprint" / "android", jsonRequest[ClientDataDto.Android]),
    response = ok(jsonResponse[FingerprintResponse]),
    docs     = EndpointDocs().withDescription("Endpoint for saving and checking fingerprint for Android.".some)
  )

  def executeFingerprintForIphone(prefix: String): Endpoint[ClientDataDto.Iphone, FingerprintResponse] = endpoint(
    request  = request(Post, root(prefix) / "fingerprint" / "iphone", jsonRequest[ClientDataDto.Iphone]),
    response = ok(jsonResponse[FingerprintResponse]),
    docs     = EndpointDocs().withDescription("Endpoint for saving and checking fingerprint for iPhone.".some)
  )
}
