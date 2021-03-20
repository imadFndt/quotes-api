package com.fndt.quote.data

import com.fndt.quote.data.util.toQuotes
import com.fndt.quote.data.util.toTag
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.repository.QuoteRepository
import org.jetbrains.exposed.sql.*

class QuoteRepositoryImpl(dbProvider: DatabaseProvider) : QuoteRepository {
    private val quotesTable: DatabaseProvider.Quotes by dbProvider
    private val tagQuoteMapTable: DatabaseProvider.TagsOnQuotes by dbProvider
    private val tagsTable: DatabaseProvider.Tags by dbProvider
    private val likesQuotesMapTable: DatabaseProvider.LikesOnQuotes by dbProvider
    private val commentTable: DatabaseProvider.Comments by dbProvider
    private val userTable: DatabaseProvider.Users by dbProvider

    override fun getQuotes(
        userId: Int?,
        isPublic: Boolean?,
        orderPopulars: Boolean,
        tagId: Int?,
        query: String?
    ): List<Quote> {
        val a = (quotesTable leftJoin userTable leftJoin tagQuoteMapTable)
            .selectAll().distinctBy { it[quotesTable.id] }
        println(a)
        return (quotesTable leftJoin userTable leftJoin tagQuoteMapTable)
            .selectAll()
            .apply {
                isPublic?.let { andWhere { quotesTable.isPublic eq it } }
                userId?.let { andWhere { quotesTable.user eq it } }
                tagId?.let { andWhere { tagQuoteMapTable.tag eq tagId } }
                query?.let { andWhere { quotesTable.body like "%$it%" } }
                orderBy(quotesTable.createdAt, SortOrder.DESC)
            }
            .groupBy(quotesTable.id)
            .map {
                it.toQuotes(
                    fetchTags(it[DatabaseProvider.Quotes.id].value),
                    fetchLikes(it[DatabaseProvider.Quotes.id].value)
                )
            }.run {
                if (orderPopulars) sortedByDescending { it.likes } else this
            }
    }

    override fun insert(body: String, userId: Int): Quote? {
        // TODO IT IN SERVICE
        userTable.select { userTable.id eq userId }.firstOrNull() ?: return null
        val id = quotesTable.insert { insert ->
            insert[quotesTable.body] = body
            insert[createdAt] = System.currentTimeMillis()
            insert[user] = userId
            insert[this.isPublic] = false
        }[quotesTable.id].value
        return findById(id)
    }

    override fun update(quoteId: Int, body: String?, isPublic: Boolean?): Quote? {
        return quotesTable.update({ quotesTable.id eq quoteId }) { update ->
            body?.let { update[DatabaseProvider.Quotes.body] = it }
            isPublic?.let { update[this.isPublic] = isPublic }
        }.let { findById(it) }
    }

    override fun removeQuote(quoteId: Int): Int {
        commentTable.deleteWhere { commentTable.quoteId eq quoteId }
        likesQuotesMapTable.deleteWhere { likesQuotesMapTable.quote eq quoteId }
        tagQuoteMapTable.deleteWhere { (tagQuoteMapTable.quote eq quoteId) }

        return quotesTable.deleteWhere { quotesTable.id eq quoteId }
    }

    override fun findById(id: Int): Quote? {
        return (quotesTable innerJoin userTable)
            .select { quotesTable.id eq id }
            .firstOrNull()
            ?.toQuotes(fetchTags(id), fetchLikes(id))
    }

    override fun findByUserId(userId: Int): List<Quote> {
        return (quotesTable innerJoin userTable)
            .select { quotesTable.user eq userId }
            .map {
                it.toQuotes(
                    fetchTags(it[DatabaseProvider.Quotes.id].value),
                    fetchLikes(it[DatabaseProvider.Quotes.id].value)
                )
            }
    }

    private fun fetchLikes(quoteId: Int): Int {
        return likesQuotesMapTable.select { DatabaseProvider.LikesOnQuotes.quote eq quoteId }
            .count().toInt()
    }

    private fun fetchTags(quoteId: Int): List<Tag> {
        return (tagQuoteMapTable innerJoin tagsTable)
            .select { tagQuoteMapTable.quote eq quoteId }
            .map { it.toTag() }
    }
}
