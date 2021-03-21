package com.fndt.quote.data

import com.fndt.quote.data.util.toLike
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.repository.LikeRepository
import org.jetbrains.exposed.sql.*

class LikeRepositoryImpl(dbProvider: DatabaseProvider) : LikeRepository {
    private val likesQuotesMapTable: DatabaseProvider.LikesOnQuotes by dbProvider

    override fun add(like: Like) {
        likesQuotesMapTable.insert { insert ->
            insert[quote] = like.quoteId
            insert[user] = like.userId
        }
    }

    override fun remove(like: Like) {
        likesQuotesMapTable.deleteWhere(op = findCondition(like)).let {
            if (it == 0) throw IllegalStateException("Delete failed")
        }
    }

    override fun find(like: Like): Like? {
        return likesQuotesMapTable.select(findCondition(like)).firstOrNull()?.toLike()
    }

    override fun getLikesForQuote(quoteId: Int): List<Like> {
        return likesQuotesMapTable.select { DatabaseProvider.LikesOnQuotes.quote eq quoteId }.map { it.toLike() }
    }

    private fun findCondition(like: Like): (SqlExpressionBuilder.() -> Op<Boolean>) = {
        (DatabaseProvider.LikesOnQuotes.user eq like.userId) and
            (DatabaseProvider.LikesOnQuotes.quote eq like.quoteId)
    }
}
