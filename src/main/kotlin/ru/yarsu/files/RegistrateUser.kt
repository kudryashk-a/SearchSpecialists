package ru.yarsu.files

import ru.yarsu.domain.operations.User
import ru.yarsu.domain.operations.Users
import java.util.*

class RegistrateUser(
    private val users: Users,
    private val settings: Settings,
) {
    operator fun invoke(
        name: String,
        login: String,
        password1: String,
        password2: String,
    ): String {
        if (password1 != password2) {
            throw RegistrationException()
        } else {
            val hashedPassword = hashPassword(password1, settings.salt)
            val id = UUID.randomUUID()
            val user = User(
                id,
                UUID.fromString("08fc459c-db6d-11ec-9d64-0242ac120002"),
                name,
                login,
                hashedPassword,
            )
            users.add(user)
            return id.toString()
        }
    }
}

class RegistrationException: RuntimeException("Пароли не совпадают")