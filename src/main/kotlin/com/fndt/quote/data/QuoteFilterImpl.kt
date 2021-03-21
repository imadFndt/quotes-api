package com.fndt.quote.data

import com.fndt.quote.data.util.nullableGroupBy
import com.fndt.quote.data.util.toQuotes
import com.fndt.quote.data.util.toTagNullable
import com.fndt.quote.domain.QuoteFilter
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User
import org.jetbrains.exposed.sql.*

class QuoteFilterImpl private constructor(
    private val tag: Tag?,
    private val user: User?,
    private val isPublic: Boolean?,
    private val orderPopulars: Boolean,
    private val query: String?,
    dbProvider: DatabaseProvider,
) : QuoteFilter() {
    private val quotesTable: DatabaseProvider.Quotes by dbProvider
    private val usersTable: DatabaseProvider.Users by dbProvider
    private val tagQuoteMapTable: DatabaseProvider.TagsOnQuotes by dbProvider
    private val tagsTable: DatabaseProvider.Tags by dbProvider
    private val likesQuotesMapTable: DatabaseProvider.LikesOnQuotes by dbProvider

    override fun getQuotes(): List<Quote> {
        return (quotesTable leftJoin usersTable leftJoin tagQuoteMapTable leftJoin tagsTable)
            .selectAll()
            .applySelectors()
            .nullableGroupBy({ it.toQuotes(likesCount = fetchLikes(it[DatabaseProvider.Quotes.id].value)) }) { it.toTagNullable() }
            .entries.map { (quote, tags) -> quote.copy(tags = tags) }
            .run { if (orderPopulars) sortedByDescending { it.likes } else this }
    }

    private fun Query.applySelectors() = apply {
        isPublic?.let { andWhere { DatabaseProvider.Quotes.isPublic eq it } }
        user?.id?.let { andWhere { DatabaseProvider.Quotes.user eq it } }
        tag?.id?.let { andWhere { DatabaseProvider.TagsOnQuotes.tag eq it } }
        query?.let { andWhere { DatabaseProvider.Quotes.body like "%$it%" } }
        orderBy(DatabaseProvider.Quotes.createdAt, SortOrder.DESC)
    }

    private fun fetchLikes(quoteId: Int): Int {
        return likesQuotesMapTable.select { DatabaseProvider.LikesOnQuotes.quote eq quoteId }
            .count().toInt()
    }

    companion object {
        fun builder(dbProvider: DatabaseProvider) = FilterBuilder(dbProvider)

        class FilterBuilder(private val dbProvider: DatabaseProvider) : Builder() {
            override fun build(): QuoteFilter = QuoteFilterImpl(tag, user, isPublic, orderPopulars, query, dbProvider)
        }
    }
}
