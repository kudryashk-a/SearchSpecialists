package ru.yarsu.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.operations.Advertisement

data class DeleteConfirm(
    val advert: Advertisement,
    val index: Int,
) : ViewModel
