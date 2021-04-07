package com.fndt.quote.data

import com.fndt.quote.data.util.toComment
import com.fndt.quote.data.util.toUser
import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.repository.CommentRepository
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class CommentRepositoryImpl(dbProvider: DatabaseProvider) : CommentRepository {
    private val commentsTable: DatabaseProvider.Comments by dbProvider
    private val usersTable: DatabaseProvider.Users by dbProvider

    override fun get(quoteId: ID): List<Comment> {
        return commentsTable
            .select { DatabaseProvider.Comments.quoteId eq quoteId }
            .orderBy(DatabaseProvider.Comments.createdAt, SortOrder.ASC)
            .map {
                val user = findUser(it[DatabaseProvider.Comments.user].value) ?: throw IllegalStateException("No user")
                it.toComment(user)
            }
    }

    override fun add(item: Comment): ID {
        return commentsTable.insert { insert ->
            insert[body] = item.body
            insert[createdAt] = item.createdAt
            insert[user] = item.user.id
            insert[quoteId] = item.quoteId
        }[commentsTable.id].value
    }

    override fun remove(item: Comment) {
        commentsTable.deleteWhere { commentsTable.id eq item.id }
    }

    override fun findById(itemId: Int): Comment? {
        return commentsTable
            .select { commentsTable.id eq itemId }
            .firstOrNull()
            ?.let {
                val user = findUser(it[DatabaseProvider.Comments.user].value) ?: throw IllegalStateException("No user")
                it.toComment(user)
            }
    }

    private fun findUser(userId: Int): User? {
        return usersTable.select { usersTable.id eq userId }.firstOrNull()?.toUser()
    }
}
