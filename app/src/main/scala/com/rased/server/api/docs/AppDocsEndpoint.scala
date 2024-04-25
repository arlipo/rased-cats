package com.rased.server.api.docs

import cats.effect.Resource
import cats.effect.kernel.Sync
import com.rased.server.api.ServerApi
import endpoints4s.openapi
import endpoints4s.openapi.model.{Info, OpenApi}
import org.http4s
import org.http4s.{HttpRoutes, StaticFile}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Location
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.server.Router

import java.util.Properties

class AppDocsEndpoint[F[_]: Sync] extends openapi.Endpoints with openapi.JsonEntitiesFromSchemas with ServerApi {
  private val dsl = Http4sDsl[F]
  import dsl._

  private val prefix = "/"

  def routes(prefix: String): HttpRoutes[F] = Router(
    prefix -> HttpRoutes.of[F] {
      case GET -> Root / "docs" / "api"                                     =>
        Ok(OpenApi.stringEncoder.encode(openApi))
      case GET -> Root / "docs" / "index.html"                              =>
        Found(Location(uri"/docs/swagger/"))
      case GET -> Root / "docs" / "swagger"                                 =>
        Found(Location(uri"/docs/swagger/"))
      case GET -> Root / "docs" / "swagger" / resource if resource.isEmpty  =>
        getSwaggerIndex
      case GET -> Root / "docs" / "swagger" / resource if resource.nonEmpty =>
        getSwaggerResource(resource)
    }
  )

  def openApi: OpenApi = openApi(
    Info(title = "Rased Server", version = "1.0.0").withDescription(
      Some("Documentation for Rased Backend service")
    )
  )(
    executeFingerprint(prefix),
    executeFingerprintForAndroid(prefix),
    executeFingerprintForIphone(prefix)
  )

  private def getSwaggerIndex: F[http4s.Response[F]] =
    StaticFile
      .fromResource("/swagger-ui/index.html")
      .map(_.withHeaders(("Transfer-Encoding", "chunked")))
      .getOrElseF(dsl.NotFound())

  private def getSwaggerResource(resource: String): F[http4s.Response[F]] =
    getSwaggerVersion.use { version =>
      StaticFile
        .fromResource(s"/META-INF/resources/webjars/swagger-ui/$version/$resource")
        .map(_.withHeaders(("Transfer-Encoding", "chunked")))
        .getOrElseF(dsl.NotFound())
    }

  private def getSwaggerVersion: Resource[F, String] = {
    val F = Sync[F]
    for {
      stream     <- Resource.fromAutoCloseable(
                      F.delay(getClass.getResourceAsStream("/META-INF/maven/org.webjars/swagger-ui/pom.properties"))
                    )
      properties <- Resource.eval(F.delay(new Properties()))
      _          <- Resource.eval(F.delay(properties.load(stream)))
      version    <- Resource.eval(F.delay(properties.getProperty("version")))
    } yield version
  }
}
