package com.fndt.quote.data.util

import com.fndt.quote.data.DatabaseProvider
import com.fndt.quote.data.DatabaseProvider.Quotes.body
import com.fndt.quote.data.DatabaseProvider.Quotes.createdAt
import com.fndt.quote.data.DatabaseProvider.TagsOnQuotes.quote
import com.fndt.quote.data.DatabaseProvider.TagsOnQuotes.tag
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Author
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun populateDb() = transaction {
    val tables = arrayOf(
        DatabaseProvider.Quotes,
        DatabaseProvider.Authors,
        DatabaseProvider.Users,
        DatabaseProvider.Tags,
        DatabaseProvider.TagsOnQuotes,
        DatabaseProvider.Comments,
        DatabaseProvider.LikesOnQuotes,
    )
    SchemaUtils.drop(*tables)
    SchemaUtils.create(*tables)
    DatabaseProvider.Authors.batchInsert(authorList) { author ->
        this[DatabaseProvider.Authors.name] = author.name
    }
    DatabaseProvider.Quotes.batchInsert(quotesList) { quote ->
        this[body] = quote.body
        this[createdAt] = System.currentTimeMillis()
        this[DatabaseProvider.Quotes.isPublic] = true
        val authorId = DatabaseProvider.Authors
            .slice(DatabaseProvider.Authors.id)
            .select { DatabaseProvider.Authors.name eq quote.author.name }
            .limit(1)
            .firstOrNull()
            ?.let { it[DatabaseProvider.Authors.id] } ?: run { throw IllegalArgumentException() }
        this[DatabaseProvider.Quotes.author] = authorId
    }
    DatabaseProvider.Users.insert { insert ->
        insert[hashedPassword] = "a".toHashed()
        insert[name] = "a"
        insert[role] = AuthRole.REGULAR
    }
    DatabaseProvider.Comments.insert { insert ->
        insert[body] = "Хуйня"
        insert[quoteId] = 1
        insert[createdAt] = System.currentTimeMillis()
        insert[user] = 1
    }
    val tags = listOf("Смешарики", "За жизнь")
    DatabaseProvider.Tags.batchInsert(tags) { tag ->
        this[DatabaseProvider.Tags.name] = tag
        this[DatabaseProvider.Tags.isPublic] = true
    }
    DatabaseProvider.TagsOnQuotes.batchInsert(tags) { current ->
        this[quote] = 1
        this[tag] = DatabaseProvider.Tags
            .slice(DatabaseProvider.Tags.id)
            .select { DatabaseProvider.Tags.name eq current }
            .firstOrNull()
            ?.let { it[DatabaseProvider.Tags.id] } ?: run { throw IllegalArgumentException() }
    }
    DatabaseProvider.TagsOnQuotes.insert { insertStatement ->
        insertStatement[quote] = 2
        insertStatement[tag] = DatabaseProvider.Tags
            .slice(DatabaseProvider.Tags.id)
            .select { DatabaseProvider.Tags.name eq "Смешарики" }
            .firstOrNull()
            ?.let { it[DatabaseProvider.Tags.id] } ?: run { throw IllegalArgumentException() }
    }
    DatabaseProvider.LikesOnQuotes.insert { insertStatement ->
        insertStatement[quote] = 2
        insertStatement[user] = 1
    }
    DatabaseProvider.LikesOnQuotes.insert { insertStatement ->
        insertStatement[quote] = 1
        insertStatement[user] = 1
    }
    DatabaseProvider.Users.batchInsert(usersList) { item ->
        this[DatabaseProvider.Users.name] = item.name
        this[DatabaseProvider.Users.hashedPassword] = item.hashedPassword
        this[DatabaseProvider.Users.role] = item.role
    }
    commit()
}

internal val authorList = listOf(
    Author(id = 1, name = "Лосяш"),
    Author(id = 2, name = "Бараш"),
    Author(id = 3, name = "Кар-карыч"),
)
internal val quotesList = listOf(
    Quote(id = 1, body = "Я просто выгляжу как лось, а в душе я бабочка.", createdAt = 0, author = Author(1, "Лосяш")),
    Quote(id = 2, body = "В вопросах дружбы размер не имеет значения.", createdAt = 0, author = Author(1, "Лосяш")),
    Quote(
        id = 3,
        body = "Почему я должен делать вид, что мне хорошо, когда мне в действительности плохо?",
        createdAt = 0,
        author = Author(0, "Бараш"),
    ),
    Quote(
        id = 4,
        body = "Нет, со мной не всё в порядке. Не бывает, чтобы у кого-то всё было в порядке.",
        createdAt = 0,
        author = Author(0, "Кар-карыч")
    ),
    Quote(
        id = 5,
        body = "Не надо строить иллюзий, которые могут закончиться травмпунктом.",
        createdAt = 0,
        author = Author(0, "Кар-карыч")
    ),
)
internal val usersList = listOf(
    User(0, "a", "a".toHashed(), role = AuthRole.REGULAR)
)
