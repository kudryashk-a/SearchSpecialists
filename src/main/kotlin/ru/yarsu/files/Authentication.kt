package ru.yarsu.files

import ru.yarsu.domain.operations.Users

class AuthenticateUserLogin(
    private val users: Users,
    private val settings: Settings,
) {
    operator fun invoke(login: String, password: String): String {
        val user = users.list().find { it.login == login } ?: throw AuthenticationException()
        val hashedPassword = hashPassword(password, settings.salt)
        if (hashedPassword != user.password)
            throw AuthenticationException()
        return user.ID.toString()
    }
}

class AuthenticationException: RuntimeException("Логин или пароль неверны")