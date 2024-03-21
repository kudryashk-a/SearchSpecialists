package ru.yarsu.files

import ru.yarsu.domain.operations.Checksum

fun hashPassword(password: String, salt: String): String{
    val saltedPassword = password + salt
    return Checksum(saltedPassword.toByteArray()).toString()
}
