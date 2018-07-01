package controllers

import scala.concurrent.ExecutionContext

import auth.DefaultEnv
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import forms.SignInForm
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.mvc.{ AbstractController, ControllerComponents }

class SignInController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv]
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

  def signIn() = silhouette.UnsecuredAction { implicit request =>
    ???
  }
}
