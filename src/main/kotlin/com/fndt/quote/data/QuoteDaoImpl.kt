package com.fndt.quote.data

import com.fndt.quote.data.util.toQuotes
import com.fndt.quote.data.util.toTag
import com.fndt.quote.domain.dao.QuoteDao
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.Tag
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

const val OPERATION_FAILED = -1

class QuoteDaoImpl(dbProvider: DatabaseProvider) : QuoteDao {
    private val quotesTable: DatabaseProvider.Quotes by dbProvider
    private val authorsTable: DatabaseProvider.Authors by dbProvider
    private val tagQuoteMapTable: DatabaseProvider.TagsOnQuotes by dbProvider
    private val tagsTable: DatabaseProvider.Tags by dbProvider
    private val likesQuotesMapTable: DatabaseProvider.LikesOnQuotes by dbProvider

    override fun getQuotes(id: Int?): List<Quote> = transaction {
        (quotesTable innerJoin authorsTable)
            .run { id?.let { select { authorsTable.id eq id } } ?: run { selectAll() } }
            .map {
                it.toQuotes(
                    fetchTags(it[DatabaseProvider.Quotes.id].value),
                    fetchLikes(it[DatabaseProvider.Quotes.id].value)
                )
            }
    }

    override fun upsertQuote(body: String, authorId: Int?, isPublic: Boolean, quoteId: Int?): Quote? = transaction {
        authorsTable.select { DatabaseProvider.Authors.id eq authorId }.firstOrNull() ?: return@transaction null
        val resultId = quoteId?.let { id ->
            quotesTable.update({ quotesTable.id eq id }) { update ->
                update[DatabaseProvider.Quotes.body] = body
                update[author] = authorId
                update[this.isPublic] = isPublic
            }
            id
        } ?: run {
            authorId ?: return@run OPERATION_FAILED
            quotesTable.insert { insert ->
                insert[DatabaseProvider.Quotes.body] = body
                insert[author] = authorId
                insert[createdAt] = System.currentTimeMillis()
                insert[this.isPublic] = isPublic
            }[quotesTable.id].value
        }
        return@transaction findById(resultId)
    }

    fun insert(body: String, authorId: Int) = transaction {
        findById(
            quotesTable.insert { insert ->
                insert[quotesTable.body] = body
                insert[author] = authorId
                insert[createdAt] = System.currentTimeMillis()
                insert[this.isPublic] = isPublic
            }[quotesTable.id].value
        )
    }

    fun update(quoteId: Int, body: String? = null, authorId: Int? = null, isPublic: Boolean = false) = transaction {
        quotesTable.update({ quotesTable.id eq quoteId }) { update ->
            body?.let { update[DatabaseProvider.Quotes.body] = it }
            authorId?.let { update[author] = it }
            update[this.isPublic] = isPublic
        }
        findById(quoteId)
    }

    override fun removeQuote(quoteId: Int): Int = transaction {
        quotesTable.deleteWhere { quotesTable.id eq quoteId }
    }

    override fun findById(id: Int): Quote? = transaction {
        quotesTable
            .slice(quotesTable.id)
            .select { quotesTable.id eq id }
            .firstOrNull()
            ?.toQuotes(fetchTags(id), fetchLikes(id))
    }

    private fun fetchLikes(quoteId: Int): Int = transaction {
        likesQuotesMapTable.select { DatabaseProvider.LikesOnQuotes.quote eq quoteId }
            .count()
            .toInt()
    }

    private fun fetchTags(quoteId: Int): List<Tag> = transaction {
        (tagQuoteMapTable innerJoin tagsTable)
            .select { tagQuoteMapTable.quote eq quoteId }
            .map { it.toTag() }
    }
}
