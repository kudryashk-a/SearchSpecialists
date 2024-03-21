package ru.yarsu.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.operations.Advertisement
import ru.yarsu.domain.operations.Pagination

data class DataList(
    val specialists: List<Advertisement>,
    val pages: Pagination,
    val number: Int,
) : ViewModel
