package com.fndt.quote.data

import com.fndt.quote.data.util.toQuotes
import com.fndt.quote.data.util.toTag
import com.fndt.quote.domain.dao.QuoteDao
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.Tag
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class QuoteDaoImpl(dbProvider: DatabaseProvider) : QuoteDao {
    private val quotesTable: DatabaseProvider.Quotes by dbProvider
    private val tagQuoteMapTable: DatabaseProvider.TagsOnQuotes by dbProvider
    private val tagsTable: DatabaseProvider.Tags by dbProvider
    private val likesQuotesMapTable: DatabaseProvider.LikesOnQuotes by dbProvider
    private val commentTable: DatabaseProvider.Comments by dbProvider
    private val userTable: DatabaseProvider.Users by dbProvider

    override fun getQuotes(userId: Int?, isPublic: Boolean?): List<Quote> = transaction {
        (quotesTable innerJoin userTable)
            .selectAll()
            .apply {
                isPublic?.let { andWhere { quotesTable.isPublic eq it } }
                userId?.let { andWhere { quotesTable.user eq it } }
            }
            .map {
                it.toQuotes(
                    fetchTags(it[DatabaseProvider.Quotes.id].value),
                    fetchLikes(it[DatabaseProvider.Quotes.id].value)
                )
            }
    }

    override fun insert(body: String, userId: Int) = transaction {
        userTable.select { userTable.id eq userId }.firstOrNull() ?: return@transaction null
        val id = quotesTable.insert { insert ->
            insert[quotesTable.body] = body
            insert[createdAt] = System.currentTimeMillis()
            insert[user] = userId
            insert[this.isPublic] = false
        }[quotesTable.id].value
        findById(id)
    }

    override fun update(quoteId: Int, body: String?, isPublic: Boolean?) = transaction {
        quotesTable.update({ quotesTable.id eq quoteId }) { update ->
            body?.let { update[DatabaseProvider.Quotes.body] = it }
            isPublic?.let { update[this.isPublic] = isPublic }
        }
        findById(quoteId)
    }

    override fun removeQuote(quoteId: Int): Int = transaction {
        commentTable.deleteWhere { commentTable.quoteId eq quoteId }
        likesQuotesMapTable.deleteWhere { likesQuotesMapTable.quote eq quoteId }
        tagQuoteMapTable.deleteWhere { (tagQuoteMapTable.quote eq quoteId) }

        quotesTable.deleteWhere { quotesTable.id eq quoteId }
    }

    override fun findById(id: Int): Quote? = transaction {
        (quotesTable innerJoin userTable)
            .select { quotesTable.id eq id }
            .firstOrNull()
            ?.toQuotes(fetchTags(id), fetchLikes(id))
    }

    override fun findByUserId(userId: Int): List<Quote> = transaction {
        (quotesTable innerJoin userTable)
            .select { quotesTable.user eq userId }
            .map {
                it.toQuotes(
                    fetchTags(it[DatabaseProvider.Quotes.id].value),
                    fetchLikes(it[DatabaseProvider.Quotes.id].value)
                )
            }
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
