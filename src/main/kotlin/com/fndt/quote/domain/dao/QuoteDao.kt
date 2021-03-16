package com.fndt.quote.domain.dao

import com.fndt.quote.domain.dto.Quote

interface QuoteDao {
    fun getQuotes(id: Int? = null): List<Quote>
    fun upsertQuote(
        body: String = "",
        authorId: Int? = null,
        isPublic: Boolean = false,
        quoteId: Int? = null
    ): Quote?

    fun removeQuote(quoteId: Int): Int
    fun findById(id: Int): Quote?
}
