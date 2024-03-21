package ru.yarsu.domain.operations

import java.io.File
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Specialists {
    var specialists = mutableListOf<Advertisement>()

    fun newNumber(): Int {
        return specialists.size
    }

    fun add(specialist: Advertisement) {
        specialists.add(specialist)
    }

    fun fetch(index: Int): Advertisement? {
        return specialists.getOrNull(index)
    }

    fun remove(index: Int) {
        specialists.removeAt(index)
        for (specialist in specialists) {
            if (specialist.ID > index) {
                specialist.ID--
            }
        }
    }

    fun edit(specialist: Advertisement) {
        val index = specialist.ID
        specialists.removeAt(index)
        for (spec in specialists) {
            if (spec.ID > index) {
                spec.ID--
            }
        }
        specialist.ID = specialists.size
        specialists.add(specialist)
    }

    fun amountPage(countPage: Int): Int {
        var count = specialists.size / countPage
        if (specialists.size % countPage != 0) {
            count += 1
        }
        return count
    }

    fun byPageNumber(pageNumber: Int, countPage: Int): List<Advertisement>
    {
        val list = mutableListOf<Advertisement>()
        for (i in 1..countPage) {
            if (i + pageNumber * countPage - 1 >= specialists.size) {
                break
            }
            list.add(specialists[i + pageNumber * countPage - 1])
        }
        return list
    }

    fun creation() {
        var identifier = 0
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")
        val formatted = LocalDateTime.parse(current.format(formatter), formatter)
        val file: InputStream = File("src\\main\\kotlin\\ru\\yarsu\\domain\\database\\specialists.csv").inputStream()
        val list = mutableListOf<String>()
        file.bufferedReader().forEachLine { list.add(it) }
        list.forEach {
            val line = it.split(",")
            val service = line[0]
            val category = line[1]
            val name = line[2]
            val phone = line[3]
            specialists.add(
                Advertisement(
                    identifier,
                    AdvertisementWithoutDate(
                        service,
                        category,
                        "",
                        name,
                        "",
                        phone,
                    ),
                    formatted,
                ),
            )
            identifier++
        }
    }
}
