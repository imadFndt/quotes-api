package com.fndt.quote.data

import com.fndt.quote.domain.QuotesEditService
import com.fndt.quote.domain.dto.Quote
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

class QuotesEditServiceImpl(
    private val quotesTable: DbProvider.Quotes,
    private val authorsTable: DbProvider.Authors
) : QuotesEditService {

    override suspend fun upsertQuote(quote: Quote) = transactionWithIO {
        quotesTable.update({ quotesTable.id eq quote.id }) {
            it[body] = quote.body
        }
        true
    }

    override suspend fun removeQuote(quoteId: Int) = transactionWithIO {
        quotesTable.deleteWhere { quotesTable.id eq quoteId } == 1
    }
}
