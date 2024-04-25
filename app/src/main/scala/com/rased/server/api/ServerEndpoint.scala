package com.rased.server.api

import cats.effect.kernel.{Concurrent, Sync}
import cats.effect.std.Random
import cats.syntax.all._
import com.rased.server.api.ServerLogging.FingerprintCalculationLogger
import com.rased.server.api.common.ExecutionLogger.ExecutionLoggerOps
import com.rased.server.domain.FingerprintModule
import com.rased.server.model.{ClientDataDto, FingerprintResponse}
import endpoints4s.http4s.server
import org.http4s
import org.http4s.HttpRoutes
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}

class ServerEndpoint[F[_]: Concurrent: Logger: Random](fingerprintModule: FingerprintModule[F])
    extends server.Endpoints[F] with server.JsonEntitiesFromSchemas with ServerApi {
  object BadRequestErr {}

  def routes(prefix: String): HttpRoutes[F] = HttpRoutes.of(
    routesFromEndpoints(
      executeFingerprint(prefix).implementedByEffect(executeFingerprintImpl),
      executeFingerprintForAndroid(prefix).implementedByEffect(executeFingerprintImpl),
      executeFingerprintForIphone(prefix).implementedByEffect(executeFingerprintImpl),
    )
  )

  def executeFingerprintImpl(data: ClientDataDto): F[FingerprintResponse] = {
    fingerprintModule.calculateFingerprint(data)
      .map(fp => FingerprintResponse(fp, none))
      .withLogging(FingerprintCalculationLogger(data))
  }

  override def handleServerError(request: http4s.Request[F], throwable: Throwable): F[http4s.Response[F]] =
    throwable match {
      case _ => throwable.raiseError
    }
}

object ServerEndpoint {
  def create[F[_]: Sync: Concurrent: Random](fingerprintModule: FingerprintModule[F]): ServerEndpoint[F] = {
    implicit val logger: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger[F]
    new ServerEndpoint[F](fingerprintModule)
  }
}
