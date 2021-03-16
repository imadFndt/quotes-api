package com.fndt.quote.data.util

import com.fndt.quote.data.DatabaseProvider
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
        id = this[DatabaseProvider.Quotes.id].value,
        body = this[DatabaseProvider.Quotes.body],
        createdAt = this[DatabaseProvider.Quotes.createdAt],
        author = Author(
            id = this[DatabaseProvider.Authors.id].value,
            name = this[DatabaseProvider.Authors.name]
        ),
        likes = likesCount,
        tags = tagList,
    )
}

fun ResultRow.toUser(): User {
    val role = AuthRole.values().find { it == this[DatabaseProvider.Users.role] } ?: AuthRole.NOT_AUTHORIZED
    return User(
        id = this[DatabaseProvider.Users.id].value,
        name = this[DatabaseProvider.Users.name],
        hashedPassword = this[DatabaseProvider.Users.hashedPassword],
        role = role
    )
}

fun ResultRow.toTag(): Tag {
    return Tag(
        id = this[DatabaseProvider.Tags.id].value,
        name = this[DatabaseProvider.Tags.name],
    )
}

fun ResultRow.toLike(): Like = Like(
    this[DatabaseProvider.LikesOnQuotes.quote].value,
    this[DatabaseProvider.LikesOnQuotes.user].value
)

fun ResultRow.toComment(): Comment {
    return Comment(
        id = this[DatabaseProvider.Comments.id].value,
        body = this[DatabaseProvider.Comments.body],
        quoteId = this[DatabaseProvider.Comments.quoteId].value,
        date = this[DatabaseProvider.Comments.createdAt],
        user = this[DatabaseProvider.Comments.user].value
    )
}

fun String.toHashed(): String {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(this.toByteArray()).decodeToString()
}
