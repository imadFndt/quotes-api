package com.fndt.quote.data

import com.fndt.quote.domain.QuotesBrowseService
import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.Tag
import org.jetbrains.exposed.sql.*

class QuotesBrowseServiceImpl(
    private val quotesTable: DbProvider.Quotes,
    private val authorsTable: DbProvider.Authors,
    private val tagsTable: DbProvider.Tags,
    private val tagQuoteMapTable: DbProvider.TagsOnQuotes,
    private val commentsTable: DbProvider.Comments,
    private val likesQuotesMapTable: DbProvider.LikesOnQuotes,
) : QuotesBrowseService {

    override suspend fun getQuotes(id: Int?): List<Quote> = transactionWithIO {
        (quotesTable innerJoin authorsTable)
            .run {
                id?.let { select { authorsTable.id eq id } } ?: run { selectAll() }
            }
            .map {
                it.toQuotes(
                    fetchTags(it[DbProvider.Quotes.id].value),
                    fetchLikes(it[DbProvider.Quotes.id].value)
                )
            }
    }

    override suspend fun setQuoteLike(like: Like, login: String): Boolean = transactionWithIO {
        val likeExists = likesQuotesMapTable.select {
            (DbProvider.LikesOnQuotes.user eq login) and (DbProvider.LikesOnQuotes.quote eq like.quoteId)
        }.firstOrNull()?.let { true } ?: run { false }
        when {
            like.likeAction && !likeExists -> {
                likesQuotesMapTable.insert { insert ->
                    insert[quote] = like.quoteId
                    insert[user] = login
                }
            }
            !like.likeAction && likeExists -> {
                likesQuotesMapTable.deleteWhere {
                    (DbProvider.LikesOnQuotes.user eq login) and (DbProvider.LikesOnQuotes.quote eq like.quoteId)
                }
            }
            else -> {
                return@transactionWithIO false
            }
        }
        commit()
        true
    }

    override suspend fun getComments(quoteId: Int): List<Comment> = transactionWithIO {
        commentsTable
            .select { DbProvider.Comments.quoteId eq quoteId }
            .map { it.toComment() }
    }

    override suspend fun addComment(commentBody: String, quoteId: Int, userName: String): Boolean = transactionWithIO {
        val id = quotesTable
            .slice(quotesTable.id)
            .select { quotesTable.id eq quoteId }
            .firstOrNull()
            ?.get(quotesTable.id)
        id ?: return@transactionWithIO false
        commentsTable.insert { insert ->
            insert[body] = commentBody
            insert[createdAt] = System.currentTimeMillis()
            insert[user] = userName
            insert[this.quoteId] = quoteId
        }
        commit()
        true
    }

    override suspend fun deleteComment(commentId: Int, userName: String): Boolean = transactionWithIO {
        commentsTable.select {
            (commentsTable.id eq commentId) and
                (commentsTable.user eq userName)
        }.firstOrNull() ?: return@transactionWithIO false
        val result = commentsTable.deleteWhere { commentsTable.id eq commentId }
        commit()
        result > 0
    }

    private fun fetchLikes(quoteId: Int): Int {
        return likesQuotesMapTable.select { DbProvider.LikesOnQuotes.quote eq quoteId }.count().toInt()
    }

    private fun fetchTags(quoteId: Int): List<Tag> {
        return (tagQuoteMapTable innerJoin tagsTable)
            .select { DbProvider.TagsOnQuotes.quote eq quoteId }
            .map { it.toTag() }
    }

    private fun ResultRow.toComment(): Comment {
        return Comment(
            id = this[DbProvider.Comments.id].value,
            body = this[DbProvider.Comments.body],
            quoteId = this[DbProvider.Comments.quoteId].value,
            date = this[DbProvider.Comments.createdAt],
            user = this[DbProvider.Comments.user].value
        )
    }
}
