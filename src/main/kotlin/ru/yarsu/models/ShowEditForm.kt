package ru.yarsu.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel

data class ShowEditForm(
    val form: WebForm = WebForm(),
): ViewModel