package ru.yarsu

import AddUser
import ShowRegistrationForm
import org.http4k.core.HttpHandler
import org.http4k.lens.RequestContextLens
import ru.ac.uniyar.web.filters.JwtTools
import ru.yarsu.domain.operations.OperateCreation
import ru.yarsu.domain.operations.Permission
import ru.yarsu.domain.operations.Specialists
import ru.yarsu.files.RegistrateUser
import ru.yarsu.routes.AuthenticateUser
import ru.yarsu.routes.ShowLoginForm
import ru.yarsu.routes.ShowUser
import ru.yarsu.routes.ShowUsers
import ru.yarsu.templates.ContextRender

class CreateHandlers(
    operateCreation: OperateCreation,
    permissionLens: RequestContextLens<Permission?>,
    htmlView: ContextRender,
    jwtTools: JwtTools,
) {
    val showUsers: HttpHandler = ShowUsers(
        permissionLens,
        operateCreation.listUserOperate,
        htmlView,
    )

    val showUser: HttpHandler = ShowUser(
        permissionLens,
        operateCreation.fetchUserOperate,
        htmlView,
    )

    val showLoginForm = ShowLoginForm(htmlView)

    val authenticateUser = AuthenticateUser(
        operateCreation.authenticateUser,
        jwtTools,
        htmlView,
    )

    val addUser = AddUser(
        htmlView,
        operateCreation.registrateUser,
        jwtTools,
    )

    val showRegistrationForm = ShowRegistrationForm(htmlView)
}