package utils

import java.util.UUID

import scala.concurrent.Future

import models.{ AuthToken, User }

object DB {
  import collection.mutable

  val users = mutable.HashMap[UUID, User]()
  val authTokens = mutable.HashMap[UUID, AuthToken]()

  def saveUser(user: User): Future[User] = {
    users += user.id -> user
    Future.successful(user)
  }

  def updateUser(user: User): Future[User] = {
    users.update(user.id, user)
    Future.successful(user)
  }

  def saveToken(token: AuthToken): Future[AuthToken] = {
    authTokens += token.id -> token
    Future.successful(token)
  }

  def findToken(tokenId: UUID): Future[Option[AuthToken]] =
    Future.successful(authTokens.find { case (tId, _) => tId == tokenId }.map(_._2))
}
