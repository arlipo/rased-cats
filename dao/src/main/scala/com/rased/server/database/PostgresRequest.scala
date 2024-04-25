package com.rased.server.database

import doobie.ConnectionIO

import java.time.Instant

trait PostgresRequest[A] {
  /**
   * Search query that is going to be sent and executed with Postgres server.
   *
   * Parameter `now` can be used in order to construct the query.
   * For more, take a look at the constructor methods available in the companion object.
   */
  def body(now: Instant): ConnectionIO[A]
}
