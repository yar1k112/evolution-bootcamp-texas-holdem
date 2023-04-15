package com.courseWork.auth

import cats.syntax.apply._
import cats.syntax.applicative._
import cats.syntax.flatMap._
import cats.{Applicative, FlatMap, Functor}
import cats.data.{Validated, ValidatedNec}

import java.util.UUID

trait AuthService[F[_]] {
  def register(nickname: String, email: String, password: String): F[User]
  def login(data: LoginData): F[Token]
}

class AuthServiceImpl[F[_]: FlatMap: Applicative: Functor](userRepository: UserRepository[F]) extends AuthService[F] {
  type UserValidationErrorsOr[A] = ValidatedNec[UserValidationError, A]

  override def register(nickname: String, email: String, password: String): F[User] = {
    validateAndGet(nickname, email, password) match {
      case Validated.Valid(user)         => userRepository.add(user)
      case Validated.Invalid(errorChain) => ??? // ae.raiseError() // todo: Make MyOwnErrors
    }
  }

  override def login(data: LoginData): F[Token] = Token("tokentokentokentokentokentokentokentoken").pure[F]

  private def validateNickname(nickname: String): UserValidationErrorsOr[String] =
    Validated.condNec[UserValidationError, String](nickname.nonEmpty, nickname, NicknameIsEmpty) *>
      Validated.condNec(nickname.length > 32, nickname, NicknameTooLong)

  private def validateEmail(email: String): F[UserValidationErrorsOr[String]] = {
    def matchesRegex(email: String): UserValidationErrorsOr[String] =
      Validated.condNec[UserValidationError, String](
        email.matches(
          "(([a-z0-9][a-z0-9-\\._]*[a-z0-9])|[a-z0-9])@(([a-z0-9][a-z0-9-\\._]*[a-z0-9])|[a-z0-9])\\.([a-z0-9]+)"
        ),
        email,
        IncorrectEmail
      )

    def userDoesntExist(email: String): F[UserValidationErrorsOr[String]] =
      userRepository.find(email).flatMap { uOpt =>
        uOpt
          .fold[UserValidationErrorsOr[String]](
            Validated.validNec(email)
          )(_ => Validated.invalidNec(UserAlreadyExist))
          .pure[F]
      }

    matchesRegex(email)
      .pure[F]
      .flatMap(matchesValidated =>
        userDoesntExist(email)
          .flatMap(userExistsValidate =>
            (userExistsValidate *> matchesValidated).pure[F]
          )
      )
  }

  private def validatePassword(password: String): UserValidationErrorsOr[String] = {
    def containsRequiredSymbols(password: String) = {
      password.contains("*") ||
      password.contains("!") ||
      password.contains("@") ||
      password.contains("#") ||
      password.contains("$") ||
      password.contains("%") ||
      password.contains("^") ||
      password.contains("&") ||
      password.contains("-") ||
      password.contains("_") ||
      password.contains("/") ||
      password.contains("{") ||
      password.contains("}") ||
      password.contains("[") ||
      password.contains("]")
    }
    def containsUpper(password: String) = password.exists(_.isUpper)
    def containsLower(password: String) = password.exists(_.isLower)
    def containsDigit(password: String) = password.exists(_.isDigit)

    Validated.condNec[UserValidationError, String](password.length >= 8, password, PasswordTooShort) *>
      Validated.condNec(password.length <= 32, password, PasswordTooLong) *>
      Validated.condNec(
        containsRequiredSymbols(password) &&
          containsUpper(password) &&
          containsLower(password) &&
          containsDigit(password),
        password,
        PasswordMissingRequiredCharacters
      )
  }

  private def validateAndGet(nickname: String, email: String, password: String): UserValidationErrorsOr[User] = {
    (
      Validated.Valid(UUID.randomUUID()),
      validateNickname(nickname),
      validateEmail(email),
      validatePassword(password)
    ).mapN(User.apply)
  }
}
