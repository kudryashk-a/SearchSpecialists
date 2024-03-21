package ru.yarsu.models

import org.http4k.template.ViewModel

data class LoginError(val description: String): ViewModel
