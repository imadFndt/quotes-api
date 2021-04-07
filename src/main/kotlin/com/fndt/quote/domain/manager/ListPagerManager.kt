package com.fndt.quote.domain.manager

import kotlin.math.ceil

class ListPagerManager(private val page: Int, private val perPage: Int) {
    fun <T> getPaged(list: List<T>): List<T> {
        val startIndex = perPage * (page - 1)
        val endIndex = (startIndex + perPage).let { if (it > list.size) list.size else it }
        return list.subList(startIndex, endIndex)
    }

    fun <T> getTotalPages(list: List<T>): Int {
        return ceil(list.size.toDouble() / perPage).toInt()
    }
}
