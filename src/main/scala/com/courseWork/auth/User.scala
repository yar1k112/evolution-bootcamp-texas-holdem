package com.courseWork.auth

import io.circe.Codec
import io.circe.generic.semiauto._

import java.util.UUID

case class User(
    uid: UUID,
    nickname: String,
    email: String,
    password: String
)

object User {
  implicit val codec: Codec[User] = deriveCodec
}
