package com.fndt.quote.data.util

import com.fndt.quote.data.DatabaseDefinition
import com.fndt.quote.data.DatabaseDefinition.Quotes.body
import com.fndt.quote.data.DatabaseDefinition.Quotes.createdAt
import com.fndt.quote.data.DatabaseDefinition.Tags.isPublic
import com.fndt.quote.data.DatabaseDefinition.Tags.name
import com.fndt.quote.data.DatabaseDefinition.TagsOnQuotes.quote
import com.fndt.quote.data.DatabaseDefinition.TagsOnQuotes.tag
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Author
import com.fndt.quote.domain.dto.Quote
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun populateDb() = transaction {
    val tables = arrayOf(
        DatabaseDefinition.Quotes,
        DatabaseDefinition.Authors,
        DatabaseDefinition.Users,
        DatabaseDefinition.Tags,
        DatabaseDefinition.TagsOnQuotes,
        DatabaseDefinition.Comments,
        DatabaseDefinition.LikesOnQuotes,
    )
    SchemaUtils.drop(*tables)
    SchemaUtils.create(*tables)
    DatabaseDefinition.Authors.batchInsert(authorList) { author ->
        this[DatabaseDefinition.Authors.name] = author.name
    }
    DatabaseDefinition.Quotes.batchInsert(quotesList) { quote ->
        this[body] = quote.body
        this[createdAt] = System.currentTimeMillis()
        val authorId = DatabaseDefinition.Authors
            .slice(DatabaseDefinition.Authors.id)
            .select { DatabaseDefinition.Authors.name eq quote.author.name }
            .limit(1)
            .firstOrNull()
            ?.let { it[DatabaseDefinition.Authors.id] } ?: run { throw IllegalArgumentException() }
        this[DatabaseDefinition.Quotes.author] = authorId
        this[isPublic] = true
    }
    DatabaseDefinition.Users.insert { insert ->
        insert[hashedPassword] = "a".toHashed()
        insert[name] = "a"
        insert[role] = AuthRole.REGULAR
    }
    DatabaseDefinition.Comments.insert { insert ->
        insert[body] = "Хуйня"
        insert[quoteId] = 1
        insert[createdAt] = System.currentTimeMillis()
        insert[user] = 1
    }
    val tags = listOf("Смешарики", "За жизнь")
    DatabaseDefinition.Tags.batchInsert(tags) { tag ->
        this[name] = tag
        this[isPublic] = true
    }
    DatabaseDefinition.TagsOnQuotes.batchInsert(tags) { current ->
        this[quote] = 1
        this[tag] = DatabaseDefinition.Tags
            .slice(DatabaseDefinition.Tags.id)
            .select { name eq current }
            .firstOrNull()
            ?.let { it[DatabaseDefinition.Tags.id] } ?: run { throw IllegalArgumentException() }
    }
    DatabaseDefinition.TagsOnQuotes.insert { insertStatement ->
        insertStatement[quote] = 2
        insertStatement[tag] = DatabaseDefinition.Tags
            .slice(DatabaseDefinition.Tags.id)
            .select { name eq "Смешарики" }
            .firstOrNull()
            ?.let { it[DatabaseDefinition.Tags.id] } ?: run { throw IllegalArgumentException() }
    }
    DatabaseDefinition.LikesOnQuotes.insert { insertStatement ->
        insertStatement[quote] = 2
        insertStatement[user] = 1
    }
    DatabaseDefinition.LikesOnQuotes.insert { insertStatement ->
        insertStatement[quote] = 1
        insertStatement[user] = 1
    }
    commit()
}

private val authorList = listOf(
    Author(id = 1, name = "Лосяш"),
    Author(id = 2, name = "Бараш"),
    Author(id = 3, name = "Кар-карыч"),
)
private val quotesList = listOf(
    Quote(id = 0, body = "Я просто выгляжу как лось, а в душе я бабочка.", createdAt = 0, author = Author(1, "Лосяш")),
    Quote(id = 0, body = "В вопросах дружбы размер не имеет значения.", createdAt = 0, author = Author(1, "Лосяш")),
    Quote(
        id = 0,
        body = "Почему я должен делать вид, что мне хорошо, когда мне в действительности плохо?",
        createdAt = 0,
        author = Author(0, "Бараш"),
    ),
    Quote(
        id = 0,
        body = "Нет, со мной не всё в порядке. Не бывает, чтобы у кого-то всё было в порядке.",
        createdAt = 0,
        author = Author(0, "Кар-карыч")
    ),
    Quote(
        id = 0,
        body = "Не надо строить иллюзий, которые могут закончиться травмпунктом.",
        createdAt = 0,
        author = Author(0, "Кар-карыч")
    ),
)
