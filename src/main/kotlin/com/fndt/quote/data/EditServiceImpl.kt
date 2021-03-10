package com.fndt.quote.data

import com.fndt.quote.domain.QuotesEditService
import com.fndt.quote.domain.dto.Quote
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

class EditServiceImpl(
    private val quotesTable: DbProvider.Quotes,
    private val authorsTable: DbProvider.Authors
) : QuotesEditService {

    override suspend fun upsertQuote(quote: Quote) = transactionWithIO {
        quotesTable.update({ quotesTable.id eq quote.id }) {
            it[body] = quote.body
            it[author] = quote.author.id
        }
        commit()
        true
    }

    override suspend fun removeQuote(quoteId: Int) = transactionWithIO {
        val deleteValue = quotesTable.deleteWhere { quotesTable.id eq quoteId }
        commit()
        deleteValue == 1
    }
}
