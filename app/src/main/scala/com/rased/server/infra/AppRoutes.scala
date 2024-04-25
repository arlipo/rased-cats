package com.rased.server.infra

import cats.Applicative
import cats.effect.kernel.Async
import cats.syntax.semigroupk._
import com.rased.server.api.ServerEndpoint
import com.rased.server.api.docs.AppDocsEndpoint
import com.rased.server.infra.middleware.NotFoundAsJson
import org.http4s.server.middleware.CORS
import org.http4s._

object AppRoutes {
  def create[F[_]: Async](
    serverEndpoint: ServerEndpoint[F],
    docsEndpoints:  AppDocsEndpoint[F]
  ): HttpApp[F] =
    CORS.policy {
      NotFoundAsJson {
        docsEndpoints.routes("/") <+>
          serverEndpoint.routes("/")
      }
    }
}
