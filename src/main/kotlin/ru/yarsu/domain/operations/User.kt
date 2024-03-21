package ru.yarsu.domain.operations

import java.util.UUID

data class User(
    val ID: UUID,
    val roleID: UUID,
    val name: String,
    val login: String,
    val password: String,
)
