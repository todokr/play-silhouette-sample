package controllers

import scala.concurrent.{ ExecutionContext, Future }

import auth.DefaultEnv
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{ LoginEvent, Silhouette }
import forms.SignInForm
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.mvc.{ AbstractController, ControllerComponents }
import services.SignInService

class SignInController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  signInService: SignInService
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport {

  def view = silhouette.UserAwareAction { implicit request =>
    request.identity.map { _ =>
      Redirect(routes.ApplicationController.mypage())
    } getOrElse {
      Ok(views.html.signIn(SignInForm.form))
    }
  }

  def signIn() = silhouette.UnsecuredAction.async { implicit request =>
    SignInForm.form.bindFromRequest().fold(
      errors => Future.successful(BadRequest(views.html.signIn(errors))),
      form => signInService.signIn(form.toSignInData).flatMap {
        case Left(_) => Future.successful(Redirect(routes.SignInController.view()))
        case Right(user) => {
          silhouette.env.eventBus.publish(LoginEvent(user, request))
          for {
            authenticator <- silhouette.env.authenticatorService.create(user.loginInfo)
            embedValue <- silhouette.env.authenticatorService.init(authenticator)
            result <- silhouette.env.authenticatorService.embed(embedValue, Redirect(routes.ApplicationController.mypage()))
          } yield result
        }
      }
    )
  }
}
