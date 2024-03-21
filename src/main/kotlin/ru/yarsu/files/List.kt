package ru.yarsu.files

import ru.yarsu.domain.operations.User
import ru.yarsu.domain.operations.Users

fun interface ListUserOperate {
    fun list(): List<User>
}

class ListUserOperation(
    val users: Users,
) : ListUserOperate {
    override fun list() = users.list()
}