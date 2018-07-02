package services

import scala.concurrent.{ ExecutionContext, Future }

import com.google.inject.Singleton
import models.User
import utils.DB

case class SignInData(
  email: String,
  password: String
)

trait SignInService {
  def signIn(data: SignInData)(implicit ec: ExecutionContext): Future[Either[String, User]]
}

@Singleton
class SignInServiceImpl extends SignInService {

  def signIn(data: SignInData)(implicit ec: ExecutionContext): Future[Either[String, User]] = {
    DB.findUser(data.email, data.password).map {
      case Some(user) => Right(user)
      case None => Left(data.email)
    }
  }
}

