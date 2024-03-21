package ru.yarsu.files

import ru.yarsu.domain.operations.Permissions
import ru.yarsu.domain.operations.User
import ru.yarsu.domain.operations.Users
import java.util.*

class FetchUserId(
    private val users: Users
) {
    operator fun invoke(token: String): User? {
        val uuid = try {
            UUID.fromString(token)
        } catch (_: IllegalArgumentException) {
            return null
        }
        return users.fetch(uuid)
    }
}

class FetchPermissionId(
    private val permissions: Permissions
) {
    operator fun invoke(roleID: UUID) = permissions.fetch(roleID)
}

fun interface FetchUserOperate {
    fun fetch(id: UUID): User?
}

class FetchUserOperation(
    val users: Users,
) : FetchUserOperate {
    override fun fetch(id: UUID): User? = users.fetch(id)
}