package com.fndt.quote.data

import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.repository.QuoteRepository
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class QuoteRepositoryImpl(dbProvider: DatabaseProvider) : QuoteRepository {
    private val quotesTable: DatabaseProvider.Quotes by dbProvider
    private val filterFactory = QuotesFilterImpl.FilterFactory(dbProvider)

    override fun get(args: QuoteFilterArguments): List<Quote> {
        return filterFactory.create().getQuotes(args)
    }

    override fun add(item: Quote): ID = transaction {
        val quoteExists = findById(item.id) != null
        return@transaction if (quoteExists) update(item) else insert(item)
    }

    override fun remove(item: Quote) {
        quotesTable.deleteWhere { quotesTable.id eq item.id }
    }

    override fun findById(itemId: Int): Quote? {
        return filterFactory.create().findQuote(QuoteFilterArguments(quoteId = itemId))
    }

    override fun findByUser(user: User): List<Quote> {
        return filterFactory.create().getQuotes(QuoteFilterArguments(user = user))
    }

    private fun insert(quote: Quote): ID {
        return quotesTable.insert { insert ->
            insert[body] = quote.body
            insert[createdAt] = quote.createdAt
            insert[user] = EntityID(quote.user.id, DatabaseProvider.Users)
            insert[isPublic] = quote.isPublic
            insert[author] = quote.author.id
        }[quotesTable.id].value
    }

    private fun update(quote: Quote): ID {
        quotesTable.update({ quotesTable.id eq quote.id }) { update ->
            update[body] = quote.body
            update[isPublic] = quote.isPublic
        }
        return quote.id
    }
}
