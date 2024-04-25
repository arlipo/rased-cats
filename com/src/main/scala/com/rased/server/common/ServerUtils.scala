package com.rased.server.common

import cats.syntax.functor._
import cats.syntax.flatMap._
import cats.syntax.applicative._
import cats.Functor
import cats.effect.{Clock, IO, Resource, Sync}
import cats.effect.kernel.Async
import cats.effect.unsafe.implicits.global
import fs2.io.file.{Files, Path}
import fs2.text
import io.circe.{Decoder, Json, parser}
import org.http4s.Uri

import java.time.{LocalDateTime, ZoneOffset}
import scala.io.Source

object ServerUtils {
  val zoneOffset: ZoneOffset = ZoneOffset.of("+2")

  def currentEstonianTime[F[_]: Clock: Functor]: F[LocalDateTime] =
    Clock[F].realTimeInstant.map(ins =>
      LocalDateTime.ofInstant(ins, zoneOffset)
    )

  def mergeMap[A, B](maps: List[Map[A, B]])(f: (B, B) => B): Map[A, B] =
    (for {
      map <- maps
      kv  <- map
    } yield kv).foldLeft(Map[A, B]()) { (acc, kv) =>
      acc + (if (acc.contains(kv._1)) kv._1 -> f(acc(kv._1), kv._2) else kv)
    }

  def writeToResource[F[_]: Async](path: String, json: Json): F[Unit] =
    writeToResource(Path(path), json)

  def writeToResource[F[_]: Async](path: Path, json: Json): F[Unit] =
    writeToFile(Path("app/src/main/resources") / path, json)

  def writeToFile[F[_]: Async](path: Path, json: Json): F[Unit] =
    Files[F]
      .writeAll(path)(fs2.Stream(json.noSpaces).covary[F].through(text.utf8.encode))
      .compile
      .drain

  def readFromResource[F[_]: Sync, A: Decoder](path: String): F[A] =
    Resource.make(
      Sync[F].blocking(Source.fromResource(path))
    )(b => Sync[F].delay(b.close())).use(source =>
      Sync[F].blocking {
        parser.parse(source.getLines().mkString).flatMap(_.as[A]) match {
          case Left(value)   => Sync[F].raiseError[A](value)
          case Right(parsed) => parsed.pure[F]
        }
      }.flatten
    )

  def readFromResourceUnsafe[A: Decoder](path: String): A = readFromResource[IO, A](path).unsafeRunSync()
  def writeToResourceUnsafe(path: String, content: Json): Unit = writeToResource[IO](Path(path), content).unsafeRunSync()

  def pathStringToPath(path: String): Uri.Path = {
    path.dropWhile(_ == '/').split("/").map(Uri.Path.Segment).foldLeft(Uri.Path.empty)(_ / _)
  }
}
