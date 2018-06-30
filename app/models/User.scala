package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }

case class User(
  id: UUID,
  firstName: String,
  lastName: String,
  email: String,
  loginInfo: LoginInfo,
  password: String,
  activated: Boolean = false) extends Identity
