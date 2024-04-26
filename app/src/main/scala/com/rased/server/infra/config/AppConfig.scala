package com.rased.server.infra.config

import cats.syntax.functor._
import cats.syntax.flatMap._
import cats.syntax.parallel._
import cats.effect.Async
import cats.effect.kernel.Sync
import ciris.{ConfigValue, env}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration.{DurationInt, FiniteDuration}

case class AppConfig(
  postgresConfig: PostgresConfig,
  mongoConfig: MongoConfig
)

object AppConfig {
  def load[F[_]: Async: Logger]: F[AppConfig] =
    for {
      env       <- Sync[F].blocking(Option(System.getenv("ENVIRONMENT")).getOrElse("local"))
      _         <- Logger[F].info(s"Loading application config for $env env")
      appConfig <- loadAppConfig
      _         <- Logger[F].info(s"Successfully loaded application config for $env env: $appConfig")
    } yield appConfig

  private def loadAppConfig[F[_]: Async]: F[AppConfig] =
    (
      postgresConfig[F],
      mongoConfig[F]
    ).parMapN { (postgres, mongo) =>
      AppConfig(
        postgresConfig = postgres,
        mongoConfig = mongo
      )
    }.load[F]

  def postgresConfig[F[_]]: ConfigValue[F, PostgresConfig] = PostgresConfig.read
  def mongoConfig[F[_]]: ConfigValue[F, MongoConfig] = MongoConfig.read
}
