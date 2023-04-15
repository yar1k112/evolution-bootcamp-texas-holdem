package com.courseWork.auth

import cats.effect.{Async, ParallelF}
import cats.syntax.functor._
import cats.syntax.flatMap._
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import io.circe.syntax.EncoderOps
import org.http4s.circe.{jsonEncoder, jsonOf}
import com.courseWork.auth.LoginData._

trait AuthRoutes[F[_]] {
  def routes: HttpRoutes[F]
}

class AuthRoutesImpl[F[_]: Async](
    authService: AuthService[F]
) extends AuthRoutes[F]
    with Http4sDsl[F] {

  implicit val loginDecoder: EntityDecoder[F, LoginData] = jsonOf[F, LoginData]
  override def routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "login" =>
        for {
          loginData <- req.as[LoginData]
          resp <- Ok(
            authService
              .login(loginData)
              .map(_.value)
          )
        } yield resp

      case GET -> Root / "register" =>
        Ok(
          authService
            .register("", "", "")
            .map(_.asJson)
        )
    }
}
