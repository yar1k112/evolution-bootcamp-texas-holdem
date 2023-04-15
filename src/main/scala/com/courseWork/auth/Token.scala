package com.courseWork.auth

case class Token(value: String) {
  override def toString: String = value
}
