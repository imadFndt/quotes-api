package com.fndt.quote.data

import com.fndt.quote.data.util.transactionWithIO
import com.fndt.quote.domain.ModeratorUserService
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Tag
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update

open class ModeratorUserServiceImpl(dbProvider: DatabaseDefinition) :
    RegularUserServiceImpl(dbProvider), ModeratorUserService {
    override suspend fun banUserTemporary(userId: Int, newRole: AuthRole, time: Int): Boolean = transactionWithIO {
        usersTable.update({ usersTable.id eq userId }) { it[blockedUntil] = System.currentTimeMillis() + time }
        true
    }

    override suspend fun setQuoteVisibility(quoteId: Int, isPublic: Boolean): Boolean = transactionWithIO {
        quotesTable.update({ quotesTable.id eq quoteId }) { it[this.isPublic] = isPublic } != 0
    }

    override suspend fun addTagForModeration(tag: Tag): Boolean = transactionWithIO {
        tagsTable.insert { insert -> insert[this.name] = tag.name }.execute(this) != 0
    }
}
