package com.fndt.quote.data

import com.fndt.quote.domain.QuotesEditService
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

class EditServiceImpl(
    private val quotesTable: DbProvider.Quotes,
    private val authorsTable: DbProvider.Authors
) : QuotesEditService {

    override suspend fun upsertQuote(
        body: String,
        authorId: Int,
        tagId: List<Int>,
        quoteId: Int?
    ): Boolean = transactionWithIO {
        authorsTable.select { DbProvider.Authors.id eq authorId }.firstOrNull() ?: return@transactionWithIO false
        val result = quoteId?.let { id ->
            quotesTable.update({ quotesTable.id eq id }) { update ->
                update[this.body] = body
                update[author] = authorId
            }
        } ?: run {
            quotesTable.insert { insert ->
                insert[this.body] = body
                insert[author] = authorId
                insert[createdAt] = System.currentTimeMillis()
            }.execute(this)
        }
        commit()
        result != null && result > 0
    }

    override suspend fun removeQuote(quoteId: Int) = transactionWithIO {
        val deleteValue = quotesTable.deleteWhere { quotesTable.id eq quoteId }
        commit()
        deleteValue == 1
    }
}
