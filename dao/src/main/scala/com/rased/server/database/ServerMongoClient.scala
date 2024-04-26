package com.rased.server.database

import cats.syntax.all._
import cats.effect.implicits.effectResourceOps
import cats.effect.{Async, Resource}
import com.rased.server.infra.config.MongoConfig
import mongo4cats.bson.Document
import mongo4cats.client.MongoClient
import mongo4cats.collection.GenericMongoCollection
import mongo4cats.database.GenericMongoDatabase

object ServerMongoClient {
  type Type[F[_]] = GenericMongoDatabase[F, ({type λ[β$0$] = fs2.Stream[F, β$0$]})#λ]
  type Collection[F[_]] = GenericMongoCollection[F, Document, ({type λ[β$0$] = fs2.Stream[F, β$0$]})#λ]

  def resource[F[_]: Async](mongoConfig: MongoConfig): Resource[F, Type[F]] =
    MongoClient.fromConnectionString[F](mongoConfig.connectionUrl)
      .flatMap(cl => cl.getDatabase(mongoConfig.databaseName).toResource)
}
