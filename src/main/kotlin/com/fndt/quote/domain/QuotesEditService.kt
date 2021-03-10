package com.fndt.quote.domain

import com.fndt.quote.domain.dto.Quote

interface QuotesEditService {
    suspend fun upsertQuote(quote: Quote): Boolean
    suspend fun removeQuote(quoteId: Int): Boolean
}
