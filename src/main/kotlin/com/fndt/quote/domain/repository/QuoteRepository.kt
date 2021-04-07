package com.fndt.quote.domain.repository

import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.repository.base.SimpleRepository

interface QuoteRepository : SimpleRepository<Quote> {
    fun get(args: QuoteFilterArguments): List<Quote>
    fun findByUser(user: User): List<Quote>
}
