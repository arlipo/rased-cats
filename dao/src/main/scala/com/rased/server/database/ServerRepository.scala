package com.rased.server.database

import com.rased.server.dao.ServerDao
import com.rased.server.model.Attributes.{Fingerprint, LastSeen}

trait ServerRepository[F[_]] {
  def upsertFingerprint(fingerprint: Fingerprint): F[Option[LastSeen]]
}

object ServerRepository {
  def create[F[_]](postgresRunner: PostgresRunner[F], serverDao: ServerDao): ServerRepository[F] =
    new ServerRepository[F] {
      def upsertFingerprint(fingerprint: Fingerprint): F[Option[LastSeen]] = postgresRunner.run(_ =>
        serverDao.upsertFingerprint(fingerprint)
      )
    }
}
