package com.fndt.quote.domain.repository

import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.QuoteFilterArguments

interface QuoteRepository {
    fun get(args: QuoteFilterArguments): List<Quote>
    fun add(quote: Quote): ID
    fun remove(quote: Quote)
    fun findById(id: Int): Quote?
    fun findByUser(user: User): List<Quote>
}
