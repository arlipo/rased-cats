package com.rased.server.infra.config

import cats.syntax.parallel._
import ciris.{ConfigValue, Effect, Secret, env}

import scala.concurrent.duration.{DurationInt, FiniteDuration}

case class PostgresConfig(
  driver: String,
  url: String,
  user: String,
  password: Secret[String],
  maxPoolSize: Int,
  maxRetries: Int,
  firstRetryDelay: FiniteDuration
)

object PostgresConfig {
  def read: ConfigValue[Effect, PostgresConfig] = (
    env("SERVER_POSTGRES_DRIVER").default("org.postgresql.Driver"),
    env("SERVER_POSTGRES_URL").or(
      (
        env("SERVER_POSTGRES_HOST"),
        env("SERVER_POSTGRES_PORT").as[Int].default(5432),
        env("SERVER_POSTGRES_DATABASE")
        ).parMapN { case (host, port, database) => s"jdbc:postgresql://$host:$port/$database" }
    ),
    env("SERVER_POSTGRES_USER").secret,
    env("SERVER_POSTGRES_PASSWORD").secret,
    env("SERVER_POSTGRES_MAX_POOL_SIZE").as[Int].default(10)
    ).parMapN { (driver, url, user, password, maxPoolSize) =>
    PostgresConfig(
      driver = driver,
      url = url,
      user = user.value,
      password = password,
      maxPoolSize = maxPoolSize,
      maxRetries = 3,
      firstRetryDelay = 100.millis
    )
  }
}
