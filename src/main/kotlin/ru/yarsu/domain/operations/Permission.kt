package ru.yarsu.domain.operations

import java.util.*

data class Permission(
    val ID: UUID,
    val name: String,
    val showSpecialists: Boolean,
    val showUsers: Boolean,
) {
    companion object{
        val GUEST = Permission(
            ID = UUID.fromString("39173158-db6b-11ec-9d64-0242ac120002"),
            name = "Гость",
            showSpecialists = false,
            showUsers = false
        )
    }
}