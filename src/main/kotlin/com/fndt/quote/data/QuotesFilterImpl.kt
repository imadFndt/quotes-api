package com.fndt.quote.data

import com.fndt.quote.data.util.nullableGroupBy
import com.fndt.quote.data.util.toAuthor
import com.fndt.quote.data.util.toQuotes
import com.fndt.quote.data.util.toTagNullable
import com.fndt.quote.domain.dto.Author
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.filter.QuotesAccess
import com.fndt.quote.domain.filter.QuotesFilter
import com.fndt.quote.domain.filter.QuotesOrder
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class QuotesFilterImpl(
    dbProvider: DatabaseProvider
) : QuotesFilter() {
    private val quotesTable: DatabaseProvider.Quotes by dbProvider
    private val usersTable: DatabaseProvider.Users by dbProvider
    private val tagQuoteMapTable: DatabaseProvider.TagsOnQuotes by dbProvider
    private val tagsTable: DatabaseProvider.Tags by dbProvider
    private val likesQuotesMapTable: DatabaseProvider.LikesOnQuotes by dbProvider
    private val authorTable: DatabaseProvider.Authors by dbProvider

    override fun getQuotes(args: QuoteFilterArguments): List<Quote> {
        return (quotesTable leftJoin usersTable leftJoin tagQuoteMapTable leftJoin tagsTable leftJoin authorTable)
            .selectAll()
            .apply { applySelectors(args) }
            .nullableGroupBy({
                it.toQuotes(
                    likesCount = fetchLikes(it[DatabaseProvider.Quotes.id].value),
                    author = findAuthor(it[DatabaseProvider.Quotes.author].value)
                )
            }) { it.toTagNullable() }
            .entries.map { (quote, tags) -> quote.copy(tags = tags) }
            .run { applyOrder(args) }
    }

    private fun findAuthor(authorId: Int): Author {
        return authorTable.select { authorTable.id eq authorId }.firstOrNull()?.toAuthor()
            ?: throw IllegalStateException("author not found")
    }

    private fun Query.applySelectors(args: QuoteFilterArguments) = apply {
        when (args.access) {
            QuotesAccess.PRIVATE -> andWhere { DatabaseProvider.Quotes.isPublic eq false }
            QuotesAccess.PUBLIC -> andWhere { DatabaseProvider.Quotes.isPublic eq true }
            QuotesAccess.ALL -> Unit
        }
        with(args) {
            user?.id?.let { andWhere { DatabaseProvider.Quotes.user eq it } }
            tag?.id?.let { andWhere { DatabaseProvider.TagsOnQuotes.tag eq it } }
            query?.let { andWhere { DatabaseProvider.Quotes.body like "%$it%" } }
            quoteId?.let { andWhere { DatabaseProvider.Quotes.id eq it } }
            authorId?.let { andWhere { DatabaseProvider.Quotes.author eq it } }
        }
    }

    private fun List<Quote>.applyOrder(args: QuoteFilterArguments): List<Quote> = when (args.order) {
        QuotesOrder.POPULARS -> sortedByDescending { it.likes }
        QuotesOrder.LATEST -> sortedByDescending { it.createdAt }
    }

    private fun fetchLikes(quoteId: Int): Int {
        return likesQuotesMapTable.select { DatabaseProvider.LikesOnQuotes.quote eq quoteId }
            .count().toInt()
    }

    class FilterFactory(private val dbProvider: DatabaseProvider) : Factory {
        override fun create(): QuotesFilter = QuotesFilterImpl(dbProvider)
    }
}
