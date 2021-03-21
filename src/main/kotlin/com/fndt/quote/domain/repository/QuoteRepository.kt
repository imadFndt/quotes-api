package com.fndt.quote.domain.repository

import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User

interface QuoteRepository {
    fun getQuotes(): List<Quote>

    fun add(quote: Quote): ID
    fun remove(quoteId: Int)
    fun findById(id: Int): Quote?
    fun findByUser(user: User): List<Quote>
}
