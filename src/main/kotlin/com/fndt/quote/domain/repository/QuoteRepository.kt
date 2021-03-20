package com.fndt.quote.domain.repository

import com.fndt.quote.domain.dto.Quote

interface QuoteRepository {
    fun getQuotes(
        userId: Int?,
        isPublic: Boolean?,
        orderPopulars: Boolean,
        tagId: Int?,
        query: String? = null,
    ): List<Quote>

    fun removeQuote(quoteId: Int): Int
    fun findById(id: Int): Quote?
    fun findByUserId(userId: Int): List<Quote>
    fun insert(body: String, userId: Int): Quote?
    fun update(quoteId: Int, body: String? = null, isPublic: Boolean? = null): Quote?
}
