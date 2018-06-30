package controllers

import java.util.UUID

import scala.concurrent.Future

import auth.DefaultEnv
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{ LoginEvent, Silhouette }
import play.api.i18n.{ I18nSupport, Messages }
import play.api.mvc.{ AbstractController, ControllerComponents }
import services.{ AuthTokenService, UserService }

class ActivateAccountController @Inject() (
  component: ControllerComponents,
  authTokenService: AuthTokenService,
  userService: UserService,
  silhouette: Silhouette[DefaultEnv]
) extends AbstractController(component) with I18nSupport {
  import scala.concurrent.ExecutionContext.Implicits.global

  def activate(tokenId: String) = silhouette.UnsecuredAction.async { implicit request =>
    authTokenService.validate(UUID.fromString(tokenId)).flatMap {
      case Some(user) =>
        userService.update(user)
        silhouette.env.eventBus.publish(LoginEvent(user, request))
        for {
          authenticator <- silhouette.env.authenticatorService.create(user.loginInfo)
          embedValue <- silhouette.env.authenticatorService.init(authenticator)
          result <- silhouette.env.authenticatorService.embed(embedValue, Redirect(routes.ApplicationController.mypage()))
        } yield result
      case None => Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("invalid.credential")))
    }
  }
}
