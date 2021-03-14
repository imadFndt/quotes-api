package com.fndt.quote.domain

interface QuotesEditService {
    suspend fun upsertQuote(body: String, authorId: Int, tagId: List<Int>, quoteId: Int? = null): Boolean

    suspend fun removeQuote(quoteId: Int): Boolean
}
