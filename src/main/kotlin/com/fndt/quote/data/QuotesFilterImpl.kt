package com.fndt.quote.data

import com.fndt.quote.data.util.*
import com.fndt.quote.domain.dto.Author
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.Access
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.filter.QuotesFilter
import com.fndt.quote.domain.filter.QuotesOrder
import org.jetbrains.exposed.sql.*

class QuotesFilterImpl(
    dbProvider: DatabaseProvider,
) : QuotesFilter() {
    private val quotesTable: DatabaseProvider.Quotes by dbProvider
    private val usersTable: DatabaseProvider.Users by dbProvider
    private val tagQuoteMapTable: DatabaseProvider.TagsOnQuotes by dbProvider
    private val tagsTable: DatabaseProvider.Tags by dbProvider
    private val likesQuotesMapTable: DatabaseProvider.LikesOnQuotes by dbProvider
    private val authorTable: DatabaseProvider.Authors by dbProvider

    private val tablesJoin get() = (quotesTable leftJoin usersTable leftJoin tagQuoteMapTable leftJoin tagsTable leftJoin authorTable)

    override fun getQuotes(args: QuoteFilterArguments): List<Quote> {
        val ids = tablesJoin
            .slice(quotesTable.id)
            .selectAll()
            .apply { applySelectors(args) }
            .map { it[DatabaseProvider.Quotes.id].value }

        return tablesJoin
            .select { quotesTable.id inList ids }
            .nullableGroupBy({
                val (likes, didILike) = fetchLikes(it[DatabaseProvider.Quotes.id].value, args.requestingUser)
                it.toQuotes(
                    likesCount = likes,
                    author = findAuthor(it[DatabaseProvider.Quotes.author].value),
                    didILike = didILike,
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
        applyAccess(args.quoteAccess, quotesTable)
        with(args) {
            user?.id?.let { andWhere { DatabaseProvider.Quotes.user eq it } }
            tagId?.let { andWhere { DatabaseProvider.TagsOnQuotes.tag eq it } }
            query?.toLowerCase()?.let { andWhere { DatabaseProvider.Quotes.body.lowerCase() like "%$it%" } }
            quoteId?.let { andWhere { DatabaseProvider.Quotes.id eq it } }
            authorId?.let { andWhere { DatabaseProvider.Quotes.author eq it } }
            quote?.let { andWhere { DatabaseProvider.Quotes.id eq it.id } }
        }
    }

    private fun List<Quote>.applyOrder(args: QuoteFilterArguments): List<Quote> = when (args.order) {
        QuotesOrder.POPULARS -> sortedByDescending { it.likes }
        QuotesOrder.LATEST -> sortedByDescending { it.createdAt }
        QuotesOrder.RANDOM -> shuffled()
    }

    private fun fetchLikes(quoteId: Int, requestingUser: User?): Pair<Int, Boolean> {
        return likesQuotesMapTable.select { DatabaseProvider.LikesOnQuotes.quote eq quoteId }
            .toList().map { it.toLike() }.run {
                return@run this.size to (this.find { it.userId == requestingUser?.id } != null)
            }
    }

    class FilterFactory(private val dbProvider: DatabaseProvider) : Factory {
        override fun create(): QuotesFilter = QuotesFilterImpl(dbProvider)
    }
}

fun Query.applyAccess(access: Access, table: DatabaseProvider.AccessLimitableIntIdTable) {
    when (access) {
        Access.PRIVATE -> andWhere { (table.isPublic eq false) }
        Access.PUBLIC -> andWhere { (table.isPublic eq true) or (table.id eq null) }
        Access.ALL -> Unit
    }
}
