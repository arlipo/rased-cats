package com.rased.server.common

import cats.effect.kernel.{Async, Resource}
import com.rased.server.config.ClientConfig
import io.netty.handler.codec.http.cookie.Cookie
import org.asynchttpclient.AsyncHttpClientConfig
import org.asynchttpclient.cookie.CookieStore
import org.asynchttpclient.uri.Uri
import org.http4s.asynchttpclient.client.AsyncHttpClient
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.client.Client

import scala.concurrent.ExecutionContext

//class ServerClient[F[_]](client: Client[F]) {}

object ServerClient {
  private def httpClientConfig(config: ClientConfig): AsyncHttpClientConfig =
    AsyncHttpClient.configure { builder =>
      builder
        .setConnectTimeout(config.connectTimeout.toMillis.toInt)
        .setReadTimeout(config.readTimeout.toMillis.toInt)
        .setRequestTimeout(config.readTimeout.toMillis.toInt)
        .setPooledConnectionIdleTimeout(config.idleTimeout.toMillis.toInt)
        .setMaxConnections(config.maxConcurrentRequests)
        .setMaxConnectionsPerHost(config.maxConcurrentRequests)
        .setCookieStore(new NoOpCookieStore)
    }

  def blazeResource[F[_]: Async](blockingContext: ExecutionContext, config: ClientConfig): Resource[F, Client[F]] = {
    BlazeClientBuilder[F]
      .withExecutionContext(blockingContext)
      .withConnectTimeout(config.connectTimeout)
      .withRequestTimeout(config.readTimeout)
      .withIdleTimeout(config.idleTimeout)
      .withMaxTotalConnections(config.maxConcurrentRequests)
      .resource
  }

  def asyncHttpResource[F[_]: Async](clientConfig: ClientConfig): Resource[F, Client[F]] =
    AsyncHttpClient.resource[F](httpClientConfig(clientConfig))

  private class NoOpCookieStore extends CookieStore {
    private def empty: java.util.List[Cookie] = java.util.Collections.emptyList[Cookie]

    override def add(uri: Uri, cookie: Cookie): Unit = ()
    override def get(uri: Uri): java.util.List[Cookie] = empty
    override def getAll: java.util.List[Cookie] = empty
    override def remove(pred: java.util.function.Predicate[Cookie]): Boolean = false
    override def clear(): Boolean = false
    override def evictExpired(): Unit = ()

    private val counter = new java.util.concurrent.atomic.AtomicInteger(0)
    override def count(): Int = counter.get
    override def decrementAndGet(): Int = counter.decrementAndGet()
    override def incrementAndGet(): Int = counter.incrementAndGet()
  }
}
