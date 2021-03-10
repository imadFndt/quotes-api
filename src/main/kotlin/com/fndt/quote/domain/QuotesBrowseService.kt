package com.fndt.quote.domain

import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote

interface QuotesBrowseService {
    suspend fun getQuotes(): List<Quote>
    suspend fun getQuotesByAuthorId(id: Int): List<Quote>
    suspend fun setQuoteLike(like: Like): Boolean
}
