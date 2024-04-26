package com.rased.server.domain

import cats.{Applicative, Monad}
import cats.effect.kernel.{Async, Sync}
import cats.effect.std.Random
import cats.syntax.all._
import com.rased.server.database.ServerMongoClient
import com.rased.server.model.Attributes.Fingerprint
import com.rased.server.model.UserDataDto
import io.circe.Json
import io.circe.syntax.EncoderOps
import mongo4cats.bson.Document
import mongo4cats.models.collection.ReplaceOptions
import mongo4cats.operations.Filter
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger

trait UserDataModule[F[_]] {
  def saveUserData(fingerprint: Fingerprint, userDataDto: UserDataDto): F[Unit]
}

object UserDataModule {
  def create[F[_]: Sync](
    mongoClient: ServerMongoClient.Type[F]
  ): F[UserDataModule[F]] = {
    implicit val logger: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger[F]
    mongoClient.getCollection("user_data").map(collection =>
      new UserDataModuleImpl[F](collection)
    )
  }

  private class UserDataModuleImpl[F[_]: Monad: Logger](mongoCollection: ServerMongoClient.Collection[F])
      extends UserDataModule[F] {
    val FINGERPRINT_KEY = "fingerprint"

    def saveUserData(fingerprint: Fingerprint, userDataDto: UserDataDto): F[Unit] = {
      val fpStr = fingerprint.value.toHexString
        mongoCollection.replaceOne(
          Filter.eq(FINGERPRINT_KEY, fpStr),
          Document.parse(userDataDto.asJson.mapObject(_.add(
            FINGERPRINT_KEY,
            Json.fromString(fpStr)
          )).noSpaces),
          ReplaceOptions(upsert = true)
        ).flatMap(res =>
          Logger[F].debug(
            s"Saving $fpStr: id: ${res.getUpsertedId}, " +
              s"ack: ${res.wasAcknowledged()}, mod: ${res.getModifiedCount}, mch: ${res.getMatchedCount}"
          )
        )
      }
  }
}
