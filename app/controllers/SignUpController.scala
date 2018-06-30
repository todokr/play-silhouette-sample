package controllers

import java.util.UUID

import scala.concurrent.{ ExecutionContext, Future }

import auth.DefaultEnv
import com.mohiva.play.silhouette.api.Silhouette
import forms.SignUpForm
import io.jsonwebtoken.Jwt
import io.jsonwebtoken.impl.DefaultJwtBuilder
import javax.inject.Inject
import org.webjars.play.WebJarsUtil
import play.api.i18n.{ I18nSupport, Messages }
import play.api.libs.mailer.{ Email, MailerClient }
import play.api.mvc.{ AbstractController, ControllerComponents }
import services.{ AuthTokenService, SignUpService }

class SignUpController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  mailerClient: MailerClient,
  signUpService: SignUpService,
  authTokenService: AuthTokenService
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport {

  def view = silhouette.UserAwareAction.async { implicit request =>
    request.identity.map { _ =>
      Future.successful(Redirect(routes.ApplicationController.mypage()))
    } getOrElse {
      Future.successful(Ok(views.html.signUp(SignUpForm.form)))
    }
  }

  def signUp = silhouette.UnsecuredAction.async { implicit request =>
    SignUpForm.form.bindFromRequest().fold(
      errors => Future.successful(BadRequest(views.html.signUp(errors))),
      formData => {
        val userId = UUID.randomUUID()
        for {
          user <- signUpService.signUp(userId, formData.toSignUpData)
          token <- authTokenService.create(userId)
        } yield {
          val url = routes.ActivateAccountController.activate(token.id.toString).absoluteURL()
          mailerClient.send(Email(
            subject = Messages("email.signup.subject"),
            from = Messages("email.from"),
            to = Seq(formData.email),
            bodyText = Some(views.txt.email.signup(user, url).body),
            bodyHtml = Some(views.html.email.signup(user, url).body)
          ))
          Redirect(routes.SignUpController.view()).flashing("info" -> Messages("sign.up.email.sent", user.email))
        }
      }
    )
  }

  def signOut = silhouette.SecuredAction.async { implicit request =>
    val result = Redirect(routes.ApplicationController.mypage())
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }
}
