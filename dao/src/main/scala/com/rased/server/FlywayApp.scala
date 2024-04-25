package com.rased.server

import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.applicativeError._
import cats.effect.{ExitCode, IO, IOApp}
import cats.effect.kernel.{Async, Sync}
import ciris.{ConfigValue, Effect}
import com.rased.server.infra.config.PostgresConfig
import org.flywaydb.core.Flyway
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger

object FlywayApp extends IOApp {
  def run(args: List[String]): IO[ExitCode] = runF[IO]

  def runF[F[_]: Async]: F[ExitCode] = {
    implicit val logger: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger[F]
    for {
      config <- postgresConfig.load[F]
      _ <- migrate(config)
    } yield ExitCode.Success
  }

  def migrate[F[_]: Sync: Logger](config: PostgresConfig): F[Unit] = {
    val flywayConf = Flyway.configure
      .sqlMigrationPrefix("V-postgres-")
      .sqlMigrationSeparator("-")
      .dataSource(config.url, config.user, config.password.value)
    Sync[F]
      .blocking(flywayConf.load.migrate())
      .void
      .attemptT
      .valueOrF(err =>
        Logger[F].warn(err.getMessage) >>
          Logger[F].warn("Repairing migration") >>
          Sync[F].blocking(flywayConf.load.repair()).void
      )
  }

  def postgresConfig: ConfigValue[Effect, PostgresConfig] = PostgresConfig.read
}
