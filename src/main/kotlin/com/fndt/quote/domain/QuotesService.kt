package com.fndt.quote.domain

import com.fndt.quote.domain.entity.Quote
import kotlinx.coroutines.flow.Flow

interface QuotesService {
    fun getQuotes(): Flow<List<Quote>>
}