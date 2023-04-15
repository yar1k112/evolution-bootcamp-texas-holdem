package com.courseWork.auth

import cats.Applicative
import cats.syntax.applicative._

trait UserRepository[F[_]] {
  def add(user: User): F[User]
  def find(email: String): F[Option[User]]
}

class UserRepositoryImpl[F[_]: Applicative] extends UserRepository[F] {

  var db = Array[User]()
  override def add(user: User): F[User] = {
    db = db.appended(user)
    user.pure[F]
  }

  override def find(email: String): F[Option[User]] = db.find(_.email == email).pure[F]
}
