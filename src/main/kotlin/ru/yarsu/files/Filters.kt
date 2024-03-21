package ru.yarsu.files

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.cookie.cookie
import org.http4k.core.with
import org.http4k.lens.BiDiLens
import org.http4k.lens.RequestContextLens
import ru.ac.uniyar.web.filters.JwtTools
import ru.yarsu.domain.operations.Permission
import ru.yarsu.domain.operations.User

fun authorizationFilter(
    userLens: RequestContextLens<User?>,
    permissionLens: RequestContextLens<Permission?>,
    fetchPermissionId: FetchPermissionId,
): Filter = Filter { next: HttpHandler ->
    { request: Request ->
        val rolePermissions = userLens(request)?.let {
            fetchPermissionId(it.roleID)
        } ?: Permission.GUEST
        val authorizedRequest = request.with(permissionLens of rolePermissions)
        next(authorizedRequest)
    }
}

fun authenticationFilter(
    user: BiDiLens<Request, User?>,
    fetchUserId: FetchUserId,
    jwtTools: JwtTools
): Filter = Filter { next: HttpHandler ->
    { request: Request ->
        val requestWithEmployee = request.cookie("token")?.value?.let { token ->
            jwtTools.subject(token)
        }?.let { userId ->
            fetchUserId(userId)
        }?.let { employee ->
                request.with(user of employee)
            } ?: request
        next(requestWithEmployee)
    }
}