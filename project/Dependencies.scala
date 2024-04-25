import sbt._

object Dependencies {
  object Cats {
    val core = "org.typelevel" %% "cats-core" % "2.9.0"
    val effect = "org.typelevel" %% "cats-effect" % "3.4.2"
    val time = "org.typelevel" %% "cats-time" % "0.5.1"

    val all = Seq(core, effect, time)
  }

  object Circe {
    private val version = "0.14.1"

    val core = "io.circe" %% "circe-core" % version
    val generic = "io.circe" %% "circe-generic" % version
    val parser = "io.circe" %% "circe-parser" % version
    val genericExtras = "io.circe" %% "circe-generic-extras" % version
    val literal = "io.circe" %% "circe-literal" % version
    val all = Seq(core, generic, parser, genericExtras, literal)
  }

  object Endpoints4s {
    private val algebraVersion = "1.5.0"
    private val jsonVersion = "1.5.0"
    private val openApiVersion = "3.1.0"
    private val clientVersion = "5.0.0"
    private val serverVersion = "7.0.0"

    val algebra = "org.endpoints4s" %% "algebra" % algebraVersion
    val algebraJson = "org.endpoints4s" %% "algebra-json-schema" % algebraVersion
    val json = "org.endpoints4s" %% "json-schema-generic" % jsonVersion
    val openApi = "org.endpoints4s" %% "openapi" % openApiVersion
    val http4sClient = "org.endpoints4s" %% "http4s-client" % clientVersion
    val http4sServer = "org.endpoints4s" %% "http4s-server" % serverVersion
    val server = Seq(algebraJson, openApi, http4sServer)
    val all = Seq(algebra, algebraJson, json, openApi, http4sClient, http4sServer)
  }

  object Http4s {
    private val version = "0.23.7"

    val blazeClient = "org.http4s" %% "http4s-blaze-client" % version
    val core = "org.http4s" %% "http4s-core" % version
    val circe = "org.http4s" %% "http4s-circe" % version
    val dsl = "org.http4s" %% "http4s-dsl" % version
    val client = "org.http4s" %% "http4s-async-http-client" % version
    val clientCore = "org.http4s" %% "http4s-client" % version
    val server = "org.http4s" %% "http4s-blaze-server" % version
    val serverCore = "org.http4s" %% "http4s-server" % version
    val all = Seq(blazeClient, core, circe, dsl, client, clientCore, server, serverCore)
  }

  object Flyway {
    private val version = "7.11.2"

    val core = "org.flywaydb" % "flyway-core" % version
    val all = List(core)
  }

  object Doobie {
    val doobieVersion = "1.0.0-RC2"

    val core =  "org.tpolecat" %% "doobie-core" % doobieVersion
    val postgres = "org.tpolecat" %% "doobie-postgres" % doobieVersion
    val hikari = "org.tpolecat" %% "doobie-hikari" % doobieVersion

    val all = Seq(core, postgres, hikari)
  }

  object Ciris {
    private val version = "2.3.1"

    val core = "is.cir" %% "ciris" % version
    val all = Seq(core)
  }

  object Log4cats {
    private val version = "2.1.1"

    val core = "org.typelevel" %% "log4cats-core" % version
    val slf4j = "org.typelevel" %% "log4cats-slf4j" % version
    val testing = "org.typelevel" %% "log4cats-testing" % version % Test
    val all = Seq(core, slf4j, testing)
  }

  object Swagger {
    private val version = "3.51.1"

    val core = "org.webjars" % "swagger-ui" % version
    val all = List(core)
  }

  object Elastic4s {
    //TODO: update elastic4s & use elastic4s-effect-cats (depends on elasticsearch version)
    private val elastic4sVersion = "7.10.3"
    private val jacksonVersion = "2.12.3"

    val core = "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion
    val client = "com.sksamuel.elastic4s" %% "elastic4s-client-esjava" % elastic4sVersion
    val jacksonModule = "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion
    val jacksonDatabind = "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
    val all = Seq(core, client, jacksonModule, jacksonDatabind)
  }
}
