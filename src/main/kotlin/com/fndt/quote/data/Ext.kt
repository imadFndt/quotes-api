package com.fndt.quote.data

import com.fndt.quote.domain.dto.Author
import com.fndt.quote.domain.dto.Quote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

suspend fun <T> transactionWithIO(block: Transaction.() -> T): T {
    return withContext(Dispatchers.IO) {
        transaction {
            this.block()
        }
    }
}

fun ResultRow.toQuotes(): Quote {
    return Quote(
        id = this[DbProvider.Quotes.id].value,
        body = this[DbProvider.Quotes.body],
        createdAt = this[DbProvider.Quotes.date],
        author = Author(
            id = this[DbProvider.Authors.id].value,
            name = this[DbProvider.Authors.name]
        ),
        likes = this[DbProvider.Quotes.likes],
    )
}
