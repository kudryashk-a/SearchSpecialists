package ru.yarsu.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.operations.User

data class ShowUsersVM(
    val users: List<User>,
) : ViewModel
