package com.fndt.quote.data.util

import com.fndt.quote.data.DatabaseDefinition
import com.fndt.quote.domain.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.MessageDigest

suspend fun <T> transactionWithIO(block: Transaction.() -> T): T {
    return withContext(Dispatchers.IO) {
        transaction {
            this.block()
        }
    }
}

fun ResultRow.toQuotes(tagList: List<Tag> = emptyList(), likesCount: Int): Quote {
    return Quote(
        id = this[DatabaseDefinition.Quotes.id].value,
        body = this[DatabaseDefinition.Quotes.body],
        createdAt = this[DatabaseDefinition.Quotes.createdAt],
        author = Author(
            id = this[DatabaseDefinition.Authors.id].value,
            name = this[DatabaseDefinition.Authors.name]
        ),
        likes = likesCount,
        tags = tagList,
    )
}

fun ResultRow.toUser(): User {
    val role = AuthRole.values().find { it == this[DatabaseDefinition.Users.role] } ?: AuthRole.NOT_AUTHORIZED
    return User(
        name = this[DatabaseDefinition.Users.name],
        hashedPassword = this[DatabaseDefinition.Users.hashedPassword],
        role = role
    )
}

fun ResultRow.toTag(): Tag {
    return Tag(
        id = this[DatabaseDefinition.Tags.id].value,
        name = this[DatabaseDefinition.Tags.name],
    )
}

fun String.toHashed(): String {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(this.toByteArray()).decodeToString()
}
