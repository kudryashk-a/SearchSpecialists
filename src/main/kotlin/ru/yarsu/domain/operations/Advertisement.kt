package ru.yarsu.domain.operations

import java.time.LocalDateTime

data class Advertisement(
    var ID: Int,
    val advertisementWithoutDate: AdvertisementWithoutDate,
    val datetime: LocalDateTime,
)
