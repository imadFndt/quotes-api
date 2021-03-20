package com.fndt.quote.data

import com.fndt.quote.data.util.toComment
import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.repository.CommentRepository
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class CommentRepositoryImpl(dbProvider: DatabaseProvider) : CommentRepository {
    private val commentsTable: DatabaseProvider.Comments by dbProvider

    override fun getComments(quoteId: Int): List<Comment> {
        return commentsTable
            .select { commentsTable.quoteId eq quoteId }
            .map { it.toComment() }
    }

    override fun insert(commentBody: String, quoteId: Int, userId: Int): Comment? {
        return findComment(
            commentsTable.insert { insert ->
                insert[body] = commentBody
                insert[createdAt] = System.currentTimeMillis()
                insert[user] = userId
                insert[this.quoteId] = quoteId
            }[commentsTable.id].value
        )
    }

    override fun remove(commentId: Int): Int {
        return commentsTable.deleteWhere { commentsTable.id eq commentId }
    }

    override fun findComment(commentId: Int): Comment? {
        return commentsTable
            .select { commentsTable.id eq commentId }
            .firstOrNull()
            ?.toComment()
    }
}
