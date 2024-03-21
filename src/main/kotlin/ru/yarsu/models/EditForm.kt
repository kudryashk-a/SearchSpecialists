package ru.yarsu.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel

data class EditForm(
    val form: WebForm = WebForm(),
    val errorName: String,
    val errorPhone: String,
) : ViewModel