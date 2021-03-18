package com.fndt.quote.domain.dao

import com.fndt.quote.domain.dto.Quote

interface QuoteDao {
    fun getQuotes(id: Int? = null, isPublic: Boolean? = null): List<Quote>
    fun removeQuote(quoteId: Int): Int
    fun findById(id: Int): Quote?
    fun findByUserId(userId: Int): List<Quote>
    fun insert(body: String, userId: Int): Quote?
    fun update(quoteId: Int, body: String? = null, isPublic: Boolean? = null): Quote?
}
