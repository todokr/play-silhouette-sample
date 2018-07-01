package forms

import play.api.data.Form
import play.api.data.Forms._
import services.SignInData

object SignInForm {

  val form = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(FormData.apply)(FormData.unapply)
  )

  case class FormData(
    email: String,
    password: String
  ) {
    def toSignInData = SignInData(email, password)
  }
}
