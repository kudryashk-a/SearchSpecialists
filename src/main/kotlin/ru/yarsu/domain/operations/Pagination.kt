package ru.yarsu.domain.operations

import org.http4k.core.Uri

class Pagination(
    var numberPage:Int,
    var countPage: Int,
    var countString: Int,
) {
    val uri = Uri.of("")

    fun listPrevPage(): Map<Int, Uri> {
        val map: MutableMap<Int, Uri> = mutableMapOf()
        var startCount = 0
        if (numberPage > 6) {
            map.put(0, uri.query(uri.toString() + "pages=0"))
            startCount = numberPage - 5
        }
        for (i in startCount..(numberPage - 1)) {
            map.put(i, uri.query(uri.toString() + "pages=" + i.toString()))
        }
        return map
    }

    fun listNextPage(): Map<Int, Uri> {
        val map: MutableMap<Int, Uri> = mutableMapOf()
        var endCount = countPage - 1
        if (numberPage + 6 < countPage) {
            endCount = numberPage + 5
        }
        for (i in numberPage + 1 ..endCount) {
            map.put(i, uri.query(uri.toString() + "page=" + i.toString()))
        }
        if (numberPage + 6 < countPage) {
            map.put(countPage - 1, uri.query(uri.toString() + "page=" + (countPage - 1).toString()))
        }
        return map
    }
}