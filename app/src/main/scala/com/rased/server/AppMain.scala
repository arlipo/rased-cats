package com.rased.server

import cats.Parallel
import cats.effect.implicits.effectResourceOps
import cats.effect.kernel.{Async, Resource}
import cats.effect.std.Random
import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.traverse._
import com.rased.server.api.ServerEndpoint
import com.rased.server.api.docs.AppDocsEndpoint
import com.rased.server.dao.postgres.ServerPostgresDao
import com.rased.server.database.{PostgresRunner, PostgresTransactor, ServerRepository}
import com.rased.server.domain.FingerprintModule
import com.rased.server.infra.AppRoutes
import com.rased.server.infra.config.AppConfig
import org.http4s.HttpApp
import org.http4s.blaze.server.BlazeServerBuilder
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}

import scala.util.chaining.scalaUtilChainingOps

object AppMain extends IOApp {

  private val banner =
    """
      |
      |   8888888b.                                  888
      |   888   Y88b                                 888
      |   888    888                                 888
      |   888   d88P  8888b.  .d8888b   .d88b.   .d88888
      |   8888888P"      "88b 88K      d8P  Y8b d88" 888
      |   888 T88b   .d888888 "Y8888b. 88888888 888  888
      |   888  T88b  888  888      X88 Y8b.     Y88b 888
      |   888   T88b "Y888888  88888P'  "Y8888   "Y88888
      |
      |""".stripMargin.split("\n").toList.appended("\n")

  def run(args: List[String]): IO[ExitCode] = runF[IO]

  def runF[F[_]: Async: Parallel]: F[ExitCode] = {
    implicit val logger: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger[F]
    for {
      _         <- banner.traverse(line => logger.info(line))
      appConfig <- AppConfig.load[F]
      _         <- FlywayApp.migrate(appConfig.postgresConfig)
      resource   = for {
        routes <- getRoutes(appConfig)
        server <- BlazeServerBuilder[F]
                    .withExecutionContext(runtime.compute)
                    .bindHttp(10000, "0.0.0.0")
                    .withHttpApp(routes)
                    .resource
      } yield server
      exitCode  <- resource.use(_ => Async[F].never[ExitCode])
    } yield exitCode
  }

  private def getRoutes[F[_]: Async: Logger: Parallel](config: AppConfig): Resource[F, HttpApp[F]] = for {
    postgresTransactor <- PostgresTransactor.create[F](config.postgresConfig)
    random             <- Random.scalaUtilRandom[F].toResource
    postgresRunner      = PostgresRunner.create[F](postgresTransactor)
    serverRepository    = ServerRepository.create[F](postgresRunner, ServerPostgresDao)
    fingerprintModule   = FingerprintModule.create[F]()
    serverEndpoint      = {
      implicit val rand: Random[F] = random
      ServerEndpoint.create[F](fingerprintModule)
    }
    docsEndpoint        = new AppDocsEndpoint[F]
  } yield AppRoutes.create(serverEndpoint, docsEndpoint)
}
