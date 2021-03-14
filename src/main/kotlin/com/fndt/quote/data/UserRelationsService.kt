package com.fndt.quote.data

import com.fndt.quote.data.util.transactionWithIO
import com.fndt.quote.domain.UserIdFinder
import org.jetbrains.exposed.sql.select

class UserRelationsService(private val userTable: DatabaseDefinition.Users) : UserIdFinder {
    override suspend fun findIdByUserName(userName: String): Int = transactionWithIO {
        userTable.slice(userTable.id).select { userTable.name eq userName }.firstOrNull()
            ?.let { it[userTable.id].value } ?: run { throw IllegalStateException(ID_NOT_FOUND) }
    }
}

const val ID_NOT_FOUND = "Id not found"
