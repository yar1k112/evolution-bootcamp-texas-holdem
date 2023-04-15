package com.courseWork

import cats.data.Kleisli
import cats.effect._
import cats.syntax.all._
import com.comcast.ip4s.IpLiteralSyntax
import com.courseWork.auth.AuthRoutes
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger

class Server[F[_] : Async](authService: AuthRoutes[F]) {

  def routes = {
    authService.routes
  }.orNotFound

  def run() = {
    val fha = Logger.httpApp(true,true)(routes)
    for {
      _ <- EmberServerBuilder.default[F]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8000")
        .withHttpApp(fha)
        .build
    } yield ()
  }.useForever
}
