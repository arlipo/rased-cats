package com.rased.server.infra.middleware

import cats.syntax.applicative._
import cats.data.Kleisli
import cats.effect.Concurrent
import com.rased.server.api.error.ErrorDto
import io.circe.Encoder
import org.http4s.{HttpApp, HttpRoutes, Request, Response, Status}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.syntax.EncoderOps
import org.http4s.circe.jsonEncoder

object NotFoundAsJson {
  def apply[F[_]: Concurrent](httpRoutes: HttpRoutes[F]): HttpApp[F] =
    Kleisli { (request: Request[F]) =>
      val response = Response[F](Status.NotFound)
        .withEntity(responseEntity(request.pathInfo.renderString).asJson)
        .pure[F]

      httpRoutes.run(request).getOrElseF(response)
    }

  private def responseEntity(path: String): ErrorDto =
    ErrorDto(
      code = "PathNotFound",
      message = "Path not found",
      path = path
    )

}
