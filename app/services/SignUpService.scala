package services

import java.util.UUID

import scala.concurrent.Future

import com.google.inject.Singleton
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.User
import utils.DB

case class SignUpData(
  firstName: String,
  lastName: String,
  email: String,
  password: String)

trait SignUpService {
  def signUp(userId: UUID, data: SignUpData): Future[User]
}

@Singleton
class SignUpServiceImpl extends SignUpService {

  def signUp(userId: UUID, data: SignUpData): Future[User] = {
    val user = User(
      id = userId,
      firstName = data.firstName,
      lastName = data.lastName,
      email = data.email,
      loginInfo = LoginInfo(CredentialsProvider.ID, data.email),
      password = data.password)
    DB.saveUser(user)
  }
}
