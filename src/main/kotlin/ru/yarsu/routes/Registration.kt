import org.http4k.core.*
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.SameSite
import org.http4k.core.cookie.cookie
import org.http4k.lens.*
import ru.ac.uniyar.web.filters.JwtTools
import ru.yarsu.files.RegistrateUser
import ru.yarsu.files.RegistrationException
import ru.yarsu.models.LoginForm
import ru.yarsu.models.RegistrationForm
import ru.yarsu.routes.AuthenticateUser
import ru.yarsu.templates.ContextRender

class ShowRegistrationForm(
    private val htmlView: ContextRender,
 ): HttpHandler {
     override fun invoke(request: Request): Response {
         return Response(Status.OK).with(htmlView(request) of RegistrationForm())
     }
 }

class AddUser(
    private val htmlView: ContextRender,
    private val registrateUser: RegistrateUser,
    private val jwtTools: JwtTools,
): HttpHandler {
    companion object {
        private val nameFormLens = FormField.nonEmptyString().required("name")
        private val loginFormLens = FormField.nonEmptyString().required("login")
        private val password1FormLens = FormField.nonEmptyString().required("password1")
        private val password2FormLens = FormField.nonEmptyString().required("password2")
        private val formLens = Body.webForm(
            Validator.Feedback,
            nameFormLens,
            loginFormLens,
            password1FormLens,
            password2FormLens,
        ).toLens()
    }
    override fun invoke(request: Request): Response {
        var webForm = formLens(request)
        if (webForm.errors.isNotEmpty()) {
            return Response(Status.OK).with(htmlView(request) of RegistrationForm(webForm))
        }
        val userId = try {registrateUser(
            nameFormLens(webForm),
            loginFormLens(webForm),
            password1FormLens(webForm),
            password2FormLens(webForm),
        )} catch(_: RegistrationException) {
            val newErrors = webForm.errors + Invalid(
                password1FormLens.meta.copy(description = "password mismatch")
            )
            webForm = webForm.copy(errors = newErrors)
            return Response(Status.OK).with(htmlView(request) of RegistrationForm(webForm))
        }
        val token = jwtTools.create(userId) ?: return Response(Status.INTERNAL_SERVER_ERROR)
        val authCookie = Cookie("token", token, httpOnly = true, sameSite = SameSite.Strict)
        return Response(Status.FOUND)
            .header("Location", "/")
            .cookie(authCookie)
    }
}
