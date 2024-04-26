package com.rased.server.api

import cats.effect.kernel.{Async, Concurrent}
import cats.effect.std.Random
import cats.syntax.all._
import com.rased.server.api.ServerLogging.FingerprintCalculationLogger
import com.rased.server.api.common.ExecutionLogger.ExecutionLoggerOps
import com.rased.server.domain.{FingerprintModule, UserDataModule}
import com.rased.server.model.Attributes.FingerprintString
import com.rased.server.model.{FingerprintResponse, UserDataDto}
import endpoints4s.http4s.server
import org.http4s
import org.http4s.HttpRoutes
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}

class ServerEndpoint[F[_]: Concurrent: Logger: Random](
  fingerprintModule: FingerprintModule[F],
  userDataModule: UserDataModule[F]
) extends server.Endpoints[F] with server.JsonEntitiesFromSchemas with ServerApi {
  object BadRequestErr {}

  def routes(prefix: String): HttpRoutes[F] = HttpRoutes.of(
    routesFromEndpoints(
      executeFingerprint(prefix).implementedByEffect(executeFingerprintImpl),
      executeFingerprintForAndroid(prefix).implementedByEffect(executeFingerprintImpl),
      executeFingerprintForIphone(prefix).implementedByEffect(executeFingerprintImpl)
    )
  )

  def executeFingerprintImpl(data: UserDataDto): F[FingerprintResponse] =
    fingerprintModule.calculateFingerprint(data)
      .flatMap(fp =>
        userDataModule.saveUserData(fp, data) >>
        fingerprintModule.updateAndCheckLastSeen(fp)
          .map(lastSeen => FingerprintResponse(FingerprintString(fp.value.toHexString), lastSeen))
      )
      .withLogging(FingerprintCalculationLogger(data))

  override def handleServerError(request: http4s.Request[F], throwable: Throwable): F[http4s.Response[F]] =
    throwable match {
      case _ => throwable.raiseError
    }
}

object ServerEndpoint {
  def create[F[_]: Async: Random](
    fingerprintModule: FingerprintModule[F],
      userDataModule: UserDataModule[F]
  ): ServerEndpoint[F] = {
    implicit val logger: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger[F]
    new ServerEndpoint[F](fingerprintModule, userDataModule)
  }
}
