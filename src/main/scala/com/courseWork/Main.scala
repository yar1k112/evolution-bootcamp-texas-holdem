package com.courseWork

import cats.effect.{ExitCode, IO, IOApp}
import com.courseWork.auth.{AuthRoutes, AuthRoutesImpl, AuthService, AuthServiceImpl, UserRepository, UserRepositoryImpl}

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val ur: UserRepository[IO] = new UserRepositoryImpl[IO]
    val as: AuthService[IO] = new AuthServiceImpl[IO](ur)
    val ar: AuthRoutes[IO] = new AuthRoutesImpl[IO](as)
    new Server[IO](ar).run().as(ExitCode.Success)
  }
}
