package com.courseWork.auth

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.EntityDecoder

case class LoginData(email: String, password: String)

object LoginData {
  implicit val codec: Codec[LoginData] = deriveCodec
}
