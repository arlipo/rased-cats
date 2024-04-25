package com.rased.server.database

import cats.Monad
import cats.syntax.flatMap._
import cats.effect.{Async, Clock}
import doobie.Transactor
import doobie.syntax.all._

trait PostgresRunner[F[_]] {
  def run[A](request: PostgresRequest[A]): F[A]
}

object PostgresRunner {
  def create[F[_]: Monad: Async: Clock](transactor: Transactor[F]): PostgresRunner[F] =
    new PostgresRunner[F] {
      override def run[A](req: PostgresRequest[A]): F[A] =
        Clock[F].realTimeInstant
          .flatMap(req.body(_).transact(transactor))
    }
}
