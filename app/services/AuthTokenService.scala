package services

import java.util.UUID

import scala.concurrent.Future

import auth.DefaultEnv
import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.{AuthToken, User}
import utils.{DB, Logger}

@Singleton
class AuthTokenService @Inject() (
  silhouette: Silhouette[DefaultEnv],
  credentialsProvider: CredentialsProvider,
  clock: Clock,
) extends Logger {
  import scala.concurrent.ExecutionContext.Implicits.global

  def create(userId: UUID, expireMinutes: Int = 30): Future[AuthToken] = {
    val expire = clock.now.plusMinutes(expireMinutes)
    val token = AuthToken(UUID.randomUUID(), userId, expire)
    logger.debug(s"token: ${token.toString}")
    DB.saveToken(token)
  }

  def validate(tokenId: UUID): Future[Option[User]] =
    DB.findToken(tokenId).map {
      case Some(token) if token.expire isAfter clock.now =>
        logger.debug(s"users ${DB.users}")
        DB.users.get(token.userId)
      case _ => None
    }
}
