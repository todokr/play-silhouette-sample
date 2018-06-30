package services

import java.util.UUID

import scala.concurrent.Future

import com.google.inject.Singleton
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import models.User
import utils.DB

@Singleton
class UserService extends IdentityService[User] {

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = Future.successful {
    DB.users.find { case (_, user) => user.loginInfo == loginInfo }.map(_._2)
  }

  def retrieve(userId: UUID): Future[Option[User]] =
    Future.successful(DB.users.get(userId))

  def update(user: User): Future[User] =
    DB.updateUser(user)
}
