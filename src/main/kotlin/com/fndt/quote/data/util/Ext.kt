package com.fndt.quote.data

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
        id = this[DbProvider.Quotes.id].value,
        body = this[DbProvider.Quotes.body],
        createdAt = this[DbProvider.Quotes.createdAt],
        author = Author(
            id = this[DbProvider.Authors.id].value,
            name = this[DbProvider.Authors.name]
        ),
        likes = likesCount,
        tags = tagList,
    )
}

fun ResultRow.toUser(): User {
    val role = AuthRole.values().find { it.byte == this[DbProvider.Users.role] } ?: AuthRole.NOT_AUTHORIZED
    return User(
        name = this[DbProvider.Users.name],
        hashedPassword = this[DbProvider.Users.hashedPassword],
        role = role
    )
}

fun ResultRow.toTag(): Tag {
    return Tag(
        id = this[DbProvider.Tags.id].value,
        name = this[DbProvider.Tags.name],
    )
}

fun String.toHashed(): String {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(this.toByteArray()).decodeToString()
}
