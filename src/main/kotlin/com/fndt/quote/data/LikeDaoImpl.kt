package com.fndt.quote.data

import com.fndt.quote.data.util.toLike
import com.fndt.quote.domain.dao.LikeDao
import com.fndt.quote.domain.dto.Like
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class LikeDaoImpl(dbProvider: DatabaseProvider) : LikeDao {
    private val likesQuotesMapTable: DatabaseProvider.LikesOnQuotes by dbProvider

    override fun like(like: Like) = transaction {
        likesQuotesMapTable.insert { insert ->
            insert[quote] = like.quoteId
            insert[user] = like.userId
        }
        find(like)
    }

    override fun unlike(like: Like): Int = transaction {
        likesQuotesMapTable.deleteWhere(op = findCondition(like))
    }

    override fun find(like: Like): Like? = transaction {
        likesQuotesMapTable.select(findCondition(like)).firstOrNull()?.toLike()
    }

    private fun findCondition(like: Like): (SqlExpressionBuilder.() -> Op<Boolean>) = {
        (DatabaseProvider.LikesOnQuotes.user eq like.userId) and
            (DatabaseProvider.LikesOnQuotes.quote eq like.quoteId)
    }
}
