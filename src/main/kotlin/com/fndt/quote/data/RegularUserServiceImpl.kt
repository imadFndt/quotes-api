package com.fndt.quote.data

import com.fndt.quote.data.util.toQuotes
import com.fndt.quote.data.util.toTag
import com.fndt.quote.data.util.transactionWithIO
import com.fndt.quote.domain.RegularUserService
import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.Tag
import org.jetbrains.exposed.sql.*

open class RegularUserServiceImpl(
    dbProvider: DatabaseDefinition
) : QuotesTablesProvider(dbProvider), RegularUserService {

    override suspend fun getQuotes(id: Int?): List<Quote> = transactionWithIO {
        (quotesTable innerJoin authorsTable)
            .run {
                id?.let { select { authorsTable.id eq id } } ?: run { selectAll() }
            }
            .map {
                it.toQuotes(
                    fetchTags(it[DatabaseDefinition.Quotes.id].value),
                    fetchLikes(it[DatabaseDefinition.Quotes.id].value)
                )
            }
    }

    override suspend fun setQuoteLike(like: Like, userId: Int): Boolean = transactionWithIO {
        val condition: (SqlExpressionBuilder.() -> Op<Boolean>) = {
            (DatabaseDefinition.LikesOnQuotes.user eq userId) and
                (DatabaseDefinition.LikesOnQuotes.quote eq like.quoteId)
        }

        val likeExists = likesQuotesMapTable.select(condition).firstOrNull()?.let { true } ?: run { false }
        when {
            like.likeAction && !likeExists -> {
                likesQuotesMapTable.insert { insert ->
                    insert[quote] = like.quoteId
                    insert[user] = userId
                }
            }
            !like.likeAction && likeExists -> {
                likesQuotesMapTable.deleteWhere(op = condition)
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
            .select { DatabaseDefinition.Comments.quoteId eq quoteId }
            .map { it.toComment() }
    }

    override suspend fun addComment(commentBody: String, quoteId: Int, userId: Int): Boolean = transactionWithIO {
        val id = quotesTable
            .slice(quotesTable.id)
            .select { quotesTable.id eq quoteId }
            .firstOrNull()
            ?.get(quotesTable.id)
        id ?: return@transactionWithIO false
        commentsTable.insert { insert ->
            insert[body] = commentBody
            insert[createdAt] = System.currentTimeMillis()
            insert[user] = userId
            insert[this.quoteId] = quoteId
        }
        commit()
        true
    }

    override suspend fun deleteComment(commentId: Int, userId: Int): Boolean = transactionWithIO {
        commentsTable.select {
            (commentsTable.id eq commentId) and
                (commentsTable.user eq userId)
        }.firstOrNull() ?: return@transactionWithIO false
        val result = commentsTable.deleteWhere { commentsTable.id eq commentId }
        commit()
        result > 0
    }

    override suspend fun upsertQuote(
        body: String,
        authorId: Int,
        tagId: List<Int>,
        quoteId: Int?
    ): Boolean = transactionWithIO {
        authorsTable.select { DatabaseDefinition.Authors.id eq authorId }.firstOrNull() ?: return@transactionWithIO false
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

    private fun fetchLikes(quoteId: Int): Int {
        return likesQuotesMapTable.select { DatabaseDefinition.LikesOnQuotes.quote eq quoteId }.count().toInt()
    }

    private fun fetchTags(quoteId: Int): List<Tag> {
        return (tagQuoteMapTable innerJoin tagsTable)
            .select { DatabaseDefinition.TagsOnQuotes.quote eq quoteId }
            .map { it.toTag() }
    }

    private fun ResultRow.toComment(): Comment {
        return Comment(
            id = this[DatabaseDefinition.Comments.id].value,
            body = this[DatabaseDefinition.Comments.body],
            quoteId = this[DatabaseDefinition.Comments.quoteId].value,
            date = this[DatabaseDefinition.Comments.createdAt],
            user = this[DatabaseDefinition.Comments.user].value
        )
    }
}
