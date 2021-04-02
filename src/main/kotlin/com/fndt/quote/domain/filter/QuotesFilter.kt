package com.fndt.quote.domain.filter

import com.fndt.quote.domain.dto.Quote

abstract class QuotesFilter {
    abstract fun getQuotes(args: QuoteFilterArguments): List<Quote>

    fun findQuote(args: QuoteFilterArguments): Quote? = getQuotes(args).firstOrNull()

    interface Factory {
        fun create(): QuotesFilter
    }
}
