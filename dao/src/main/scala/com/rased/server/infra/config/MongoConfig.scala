package com.rased.server.infra.config

import cats.implicits.catsSyntaxTuple2Parallel
import ciris.{ConfigValue, Effect, env}

case class MongoConfig(
  connectionUrl: String,
  databaseName: String
)

object MongoConfig {
  def read: ConfigValue[Effect, MongoConfig] = (
    env("SERVER_MONGO_CONNECTION_URL"),
    env("SERVER_MONGO_DATABASE_NAME")
    ).parMapN { (connectionUrl, databaseName) =>
    MongoConfig(
      connectionUrl = connectionUrl,
      databaseName = databaseName
    )
  }
}
