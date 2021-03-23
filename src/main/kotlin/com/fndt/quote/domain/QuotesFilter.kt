package com.fndt.quote.domain

import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User

abstract class QuotesFilter {
    var tag: Tag? = null
    var user: User? = null
    var isPublic: Boolean? = null
    var orderPopulars: Boolean = false
    var query: String? = null
    var quoteId: Int? = null

    abstract fun getQuotes(): List<Quote>

    fun findQuote(): Quote? = getQuotes().firstOrNull()

    interface Factory {
        fun create(): QuotesFilter
    }
}
