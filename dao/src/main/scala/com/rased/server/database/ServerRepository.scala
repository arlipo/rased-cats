package com.rased.server.database

import com.rased.server.dao.ServerDao

trait ServerRepository[F[_]] {
}

object ServerRepository {
  def create[F[_]](postgresRunner: PostgresRunner[F], serverDao: ServerDao): ServerRepository[F] =
    new ServerRepository[F] {}
}
