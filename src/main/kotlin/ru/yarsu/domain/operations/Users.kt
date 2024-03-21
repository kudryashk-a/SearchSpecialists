package ru.yarsu.domain.operations

import java.util.UUID

class Users(users: List<User>) {
    val users = users.associateBy { it.ID }.toMutableMap()

    fun fetch(id: UUID): User? = users[id]

    fun list() = users.values.toList()

    fun add(user: User) {
        users.put(user.ID, user)
    }
}