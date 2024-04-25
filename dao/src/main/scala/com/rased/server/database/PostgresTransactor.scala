package com.rased.server.database

import cats.effect.{Async, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import com.rased.server.infra.config.PostgresConfig

object PostgresTransactor {
  def create[F[_]: Async](
    config: PostgresConfig
  ): Resource[F, HikariTransactor[F]] =
    ExecutionContexts
      .fixedThreadPool(config.maxPoolSize)
      .flatMap { connectEC =>
        HikariTransactor.newHikariTransactor(
          driverClassName = config.driver,
          url = config.url,
          user = config.user,
          pass = config.password.value,
          connectEC = connectEC
        )
      }
}
