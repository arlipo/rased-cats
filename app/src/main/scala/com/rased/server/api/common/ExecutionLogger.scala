package com.rased.server.api.common

import cats.syntax.monadError._
import cats.syntax.applicativeError._
import cats.syntax.flatMap._
import cats.{Functor, Monad, MonadThrow}
import cats.effect.std.Random
import com.rased.server.api.error.ExpectedException
import org.typelevel.log4cats.Logger

import scala.util.chaining.scalaUtilChainingOps

private[api] trait ExecutionLogger {
  def logStart: String
  def log2xx: String
  def log4xx(reason: String): String
  def log5xx(ex:     Throwable): String
}

private[api] object ExecutionLogger {

  def withLogging[F[_]: MonadThrow: Logger: Random, A](logger: ExecutionLogger)(fa: F[A]): F[A] =
    withUID { uid =>
      val prefix = s"$uid - "

      Logger[F].info(prefix + logger.logStart) >> fa.attempt.flatTap {
        case Right(_)                          =>
          Logger[F].info(prefix + logger.log2xx)
        case Left(expected: ExpectedException) =>
          Logger[F].info(prefix + logger.log4xx(expected.getMessage))
        case Left(ex)                          =>
          Logger[F].info(prefix + logger.log5xx(ex))
      }.rethrow
    }

  private def withUID[F[_]: Random: Monad, A](f: String => F[A]): F[A] =
    Random[F].nextIntBounded(0xffff).flatMap(int =>
      f(int.toHexString.pipe(str => "0" * (4 - str.length) + str))
    )

  implicit class ExecutionLoggerOps[F[_]: MonadThrow: Logger: Random, A](fa: F[A]) {
    def withLogging(executionLogger: ExecutionLogger): F[A] =
      ExecutionLogger.withLogging(executionLogger)(fa)
  }
}
