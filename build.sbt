import Dependencies.{Log4cats, _}
import sbt.Keys.libraryDependencies

import scala.collection.Seq

ThisBuild / envFileName := s".env"

lazy val commonSettings = Seq(
  scalaVersion := "2.13.10",
  publishTo := Some(
    "Docker Hub"
      .at("https://hub.docker.com/repository/docker/")
  ),
)

lazy val dockerSettings = Seq(
  dockerBaseImage := "adoptopenjdk/openjdk11@sha256:3e8d34280cb2eaabded54093ff579070d2a991de5b1bc09d69637f69dd7c00c3",
  Docker / version := "latest",
  credentials += Credentials(Path.userHome / ".sbt" / ".dockerCredentials")
  // Will be provided later // dockerRepository :=
)

lazy val root =
  project
    .in(file("."))
    .settings(
      name := "rased"
    )
    .settings(commonSettings)
    .settings(
      publish := {},
      publishLocal := {},
      Docker / publish := {},
      Docker / publishLocal := {},
    )
    .aggregate(app, dao, mod, com)

lazy val app =
  project
    .in(file("app"))
    .enablePlugins(
      JavaAppPackaging,
      DockerPlugin
    )
    .settings(commonSettings, dockerSettings)
    .settings(
      libraryDependencies ++= Cats.all ++ Circe.all ++ Endpoints4s.all ++ Http4s.all ++ Ciris.all ++ Swagger.all,
      // Will be provided later // Docker / packageName :=
      dockerExposedPorts := Seq(10000),
      dockerExposedVolumes := Seq("/opt/docker/.logs", "/opt/docker/.keys"),
    )
    .dependsOn(dao, com)

lazy val dao =
  project
    .in(file("dao"))
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Flyway.all ++ Cats.all ++ Ciris.all ++ Doobie.all ++ Log4cats.all,
      libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.5.6"
    )
    .settings(
      publish := {},
      publishLocal := {},
      Docker / publish := {},
      Docker / publishLocal := {},
    )
    .dependsOn(mod)

lazy val mod =
  project
    .in(file("mod"))
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Circe.all
    )
    .settings(
      publish := {},
      publishLocal := {},
      Docker / publish := {},
      Docker / publishLocal := {},
    )
    .dependsOn(com)

lazy val com =
  project
    .in(file("com"))
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Http4s.all ++ Circe.all
    )
    .settings(
      publish := {},
      publishLocal := {},
      Docker / publish := {},
      Docker / publishLocal := {},
    )
