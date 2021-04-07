package com.fndt.quote.data.util

import com.fndt.quote.data.DatabaseProvider
import com.fndt.quote.domain.dto.*
import org.jetbrains.exposed.sql.ResultRow
import java.security.MessageDigest

fun ResultRow.toQuotes(
    tagList: List<Tag> = emptyList(),
    likesCount: Int,
    author: Author,
): Quote {
    return Quote(
        id = this[DatabaseProvider.Quotes.id].value,
        body = this[DatabaseProvider.Quotes.body],
        createdAt = this[DatabaseProvider.Quotes.createdAt],
        isPublic = this[DatabaseProvider.Quotes.isPublic],
        user = toUser(),
        likes = likesCount,
        tags = tagList,
        author = author
    )
}

fun ResultRow.toUser(withPassword: Boolean = false): User {
    val role = AuthRole.values().find { it == this[DatabaseProvider.Users.role] } ?: AuthRole.NOT_AUTHORIZED
    return User(
        id = this[DatabaseProvider.Users.id].value,
        name = this[DatabaseProvider.Users.name],
        role = role,
        blockedUntil = this[DatabaseProvider.Users.blockedUntil],
        avatarScheme = this[DatabaseProvider.Users.avatarScheme]
    ).also {
        if (withPassword) it.hashedPassword = this[DatabaseProvider.Users.hashedPassword]
    }
}

fun ResultRow.toTag(): Tag {
    return Tag(
        id = this[DatabaseProvider.Tags.id].value,
        name = this[DatabaseProvider.Tags.name],
        isPublic = this[DatabaseProvider.Tags.isPublic]
    )
}

fun ResultRow.toLike(): Like = Like(
    this[DatabaseProvider.LikesOnQuotes.quote].value,
    this[DatabaseProvider.LikesOnQuotes.user].value
)

fun ResultRow.toComment(user: User): Comment {
    return Comment(
        id = this[DatabaseProvider.Comments.id].value,
        body = this[DatabaseProvider.Comments.body],
        quoteId = this[DatabaseProvider.Comments.quoteId].value,
        createdAt = this[DatabaseProvider.Comments.createdAt],
        user = user
    )
}

fun ResultRow.toAuthor() = Author(
    this[DatabaseProvider.Authors.id].value,
    this[DatabaseProvider.Authors.name],
)

// NOT REDUNDANT
fun ResultRow.toTagNullable(): Tag? {
    return try {
        toTag()
    } catch (e: NullPointerException) {
        null
    }
}

fun String.toHashed(): String {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(this.toByteArray()).decodeToString()
}

inline fun <T, K, V> Iterable<T>.nullableGroupBy(keySelector: (T) -> K, valueTransform: (T) -> V?): Map<K, List<V>> {
    return groupByTo(LinkedHashMap(), keySelector, valueTransform)
}

inline fun <T, K, V, M : MutableMap<in K, MutableList<V>>> Iterable<T>.groupByTo(
    destination: M,
    keySelector: (T) -> K,
    valueTransform: (T) -> V?
): M {
    for (element in this) {
        val key = keySelector(element)
        val list = destination.getOrPut(key) { ArrayList() }
        valueTransform(element)?.let { list.add(it) }
    }
    return destination
}
