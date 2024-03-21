package ru.yarsu.routes

import org.http4k.core.*
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.RequestContextLens
import org.http4k.routing.path
import org.http4k.template.ViewModel
import ru.yarsu.domain.operations.Permission
import ru.yarsu.domain.operations.User
import ru.yarsu.files.FetchUserOperate
import ru.yarsu.files.ListUserOperate
import ru.yarsu.models.ShowUserVM
import ru.yarsu.models.ShowUsersVM
import ru.yarsu.templates.ContextRender
import java.util.*

class ShowUser(
    private val permissionLens: RequestContextLens<Permission?>,
    private val fetchUserOperate: FetchUserOperate,
    private val htmlView: ContextRender,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val id = request.path("id")?.let {
            UUID.fromString(it)
        }?.let {
            fetchUserOperate.fetch(it)
        } ?: return Response(Status.BAD_REQUEST)
        val permissions = permissionLens(request)
        if (!permissions!!.showUsers)
            Response(Status.UNAUTHORIZED)
        return Response(Status.OK).with(
            htmlView(request) of ShowUserVM(user = id)
        )
    }
}

class ShowUsers(
    private val permissionLens: RequestContextLens<Permission?>,
    private val listUserOperate: ListUserOperate,
    private val htmlView: ContextRender,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val permission = permissionLens(request)
        if (!permission!!.showUsers)
            return Response(Status.UNAUTHORIZED)
        val users = listUserOperate.list()
        return Response(Status.OK).with(htmlView(request) of ShowUsersVM(users))
    }
}