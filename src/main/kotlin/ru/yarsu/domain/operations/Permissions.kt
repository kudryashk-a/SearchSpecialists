package ru.yarsu.domain.operations

import java.util.*

class Permissions(permissions: List<Permission>) {
    val permissions = permissions.associateBy { it.ID }.toMutableMap()

    fun fetch(id: UUID): Permission? = permissions[id]
}