package com.fndt.quote.data

import com.fndt.quote.domain.QuotesBrowseService
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class QuotesBrowseServiceImpl(
    private val quotesTable: DbProvider.Quotes,
    private val authorsTable: DbProvider.Authors
) : QuotesBrowseService {

    override suspend fun getQuotes(): List<Quote> = transactionWithIO {
        (quotesTable innerJoin authorsTable)
            .selectAll()
            .map { it.toQuotes() }
    }

    override suspend fun getQuotesByAuthorId(id: Int): List<Quote> = transactionWithIO {
        (quotesTable innerJoin authorsTable)
            .select { authorsTable.id eq id }
            .map { it.toQuotes() }
    }

    override suspend fun setQuoteLike(like: Like): Boolean = transactionWithIO {
        var likeCount = quotesTable
            .slice(quotesTable.likes)
            .select { quotesTable.id eq like.quoteId }
            .firstOrNull()
            ?.get(quotesTable.likes)
        likeCount ?: return@transactionWithIO false
        // TODO CHECK USER LIKE BEFORE LIKING
        if (like.likeAction) likeCount++ else likeCount--
        quotesTable.update({ quotesTable.id eq like.quoteId }) { it[likes] = likeCount }
        commit()
        true
    }
}
