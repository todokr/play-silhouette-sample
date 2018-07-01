package services

import scala.concurrent.Future

import models.User

case class SignInData(
  email: String,
  password: String
)

trait SignInService {
  def signIn(data: SignInData): Future[Either[Throwable, User]]
}

