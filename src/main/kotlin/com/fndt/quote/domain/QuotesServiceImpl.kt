package com.fndt.quote.domain

import com.fndt.quote.domain.entity.Quote
import kotlinx.coroutines.flow.flow

class QuotesServiceImpl : QuotesService {
    override fun getQuotes() = flow<List<Quote>> {
        val quote1: Quote = Quote("a", 1L)
        val quote2: Quote = Quote("b", 2L)
        emit(listOf(quote1, quote2, quote1, quote2, quote1, quote2))
    }
}