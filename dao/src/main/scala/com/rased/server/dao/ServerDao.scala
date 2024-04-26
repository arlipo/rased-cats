package com.rased.server.dao

import com.rased.server.model.Attributes.{Fingerprint, LastSeen}
import doobie.free.connection
import doobie.free.connection.ConnectionOp
import doobie.Fragments.{in, notIn, whereAnd, whereAndOpt}
import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator
import doobie.util.log.LogHandler
import doobie.{Fragment, Meta}
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.postgres._
import doobie.postgres.implicits._

trait ServerDao {
  def upsertFingerprint(fingerprint: Fingerprint): ConnectionIO[Option[LastSeen]]
}

object ServerDao extends ServerDao {
  def upsertFingerprint(fingerprint: Fingerprint): ConnectionIO[Option[LastSeen]] = for {
    lastSeenOpt <- requestLastSeenByFingerprint(fingerprint)
    _ <- lastSeenOpt.fold(insertFingerprint(fingerprint))(_ => updateFingerprint(fingerprint))
  } yield lastSeenOpt

  private def updateFingerprint(fingerprint: Fingerprint): ConnectionIO[Int] =
    sql"""
        UPDATE fingerprint_events fe
        SET last_seen = CURRENT_TIMESTAMP
        WHERE fe.fingerprint = $fingerprint
       """.update.run

  private def insertFingerprint(fingerprint: Fingerprint): ConnectionIO[Int] =
    sql"""
        INSERT INTO fingerprint_events (fingerprint)
        VALUES ($fingerprint);
       """.update.run

  private def requestLastSeenByFingerprint(fingerprint: Fingerprint): ConnectionIO[Option[LastSeen]] =
    sql"""
        SELECT last_seen FROM fingerprint_events fe WHERE fe.fingerprint = $fingerprint
         """.query[LastSeen].option
}
