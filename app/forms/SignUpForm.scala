package forms

import play.api.data.Form
import play.api.data.Forms._
import services.SignUpData

object SignUpForm {

  val form = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText
    )(FormData.apply)(FormData.unapply))

  case class FormData(
    firstName: String,
    lastName: String,
    email: String,
    password: String
  ) {
    def toSignUpData: SignUpData = SignUpData(
      firstName = firstName,
      lastName = lastName,
      email = email,
      password = password
    )
  }
}
