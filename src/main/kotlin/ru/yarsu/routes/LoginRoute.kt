package ru.yarsu.routes

import org.http4k.core.*
import org.http4k.core.Status.Companion.OK
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.SameSite
import org.http4k.core.cookie.cookie
import org.http4k.lens.*
import ru.ac.uniyar.web.filters.JwtTools
import ru.yarsu.files.AuthenticateUserLogin
import ru.yarsu.files.AuthenticationException
import ru.yarsu.models.LoginForm
import ru.yarsu.templates.ContextRender

class ShowLoginForm(
    private val htmlView: ContextRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        return Response(OK).with(htmlView(request) of LoginForm())
    }
}

class AuthenticateUser(
    private val authenticateUser: AuthenticateUserLogin,
    private val jwtTools: JwtTools,
    private val htmlView: ContextRender,
): HttpHandler {
    companion object {
        private val loginFormLens = FormField.nonEmptyString().required("login")
        private val passwordFormLens = FormField.nonEmptyString().required("password")
        private val formLens = Body.webForm(
            Validator.Feedback,
            loginFormLens,
            passwordFormLens,
        ).toLens()
    }
    override fun invoke(request: Request): Response {
        var webForm = formLens(request)
        if (webForm.errors.isNotEmpty()) {
            return Response(OK).with(htmlView(request) of LoginForm(webForm))
        }
        val userId = try {
            authenticateUser(loginFormLens(webForm), passwordFormLens(webForm))
        } catch (_: AuthenticationException) {
            val newErrors = webForm.errors + Invalid(
                passwordFormLens.meta.copy(description = "wrong login or password")
            )
            webForm = webForm.copy(errors = newErrors)
            return Response(OK).with(htmlView(request) of LoginForm(webForm))
        }
        val token = jwtTools.create(userId) ?: return Response(Status.INTERNAL_SERVER_ERROR)
        val authCookie = Cookie("token", token, httpOnly = true, sameSite = SameSite.Strict)
        return Response(Status.FOUND)
            .header("Location", "/")
            .cookie(authCookie)
    }
}
