package com.fndt.quote.data

import com.fndt.quote.data.DbProvider.Quotes.body
import com.fndt.quote.data.DbProvider.Quotes.createdAt
import com.fndt.quote.data.DbProvider.Tags.isPublic
import com.fndt.quote.data.DbProvider.Tags.name
import com.fndt.quote.data.DbProvider.TagsOnQuotes.quote
import com.fndt.quote.data.DbProvider.TagsOnQuotes.tag
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
        DbProvider.Quotes,
        DbProvider.Authors,
        DbProvider.Users,
        DbProvider.Tags,
        DbProvider.TagsOnQuotes,
        DbProvider.Comments,
        DbProvider.LikesOnQuotes,
    )
    SchemaUtils.drop(*tables)
    SchemaUtils.create(*tables)
    DbProvider.Authors.batchInsert(authorList) { author ->
        this[DbProvider.Authors.name] = author.name
    }
    DbProvider.Quotes.batchInsert(quotesList) { quote ->
        this[body] = quote.body
        this[createdAt] = System.currentTimeMillis()
        val authorId = DbProvider.Authors
            .slice(DbProvider.Authors.id)
            .select { DbProvider.Authors.name eq quote.author.name }
            .limit(1)
            .firstOrNull()
            ?.let { it[DbProvider.Authors.id] } ?: run { throw IllegalArgumentException() }
        this[DbProvider.Quotes.author] = authorId
        this[isPublic] = true
    }
    DbProvider.Users.insert { insert ->
        insert[hashedPassword] = "a".toHashed()
        insert[name] = "a"
        insert[role] = AuthRole.REGULAR.byte
    }
    DbProvider.Comments.insert { insert ->
        insert[body] = "Хуйня"
        insert[quoteId] = 1
        insert[createdAt] = System.currentTimeMillis()
        insert[user] = "a"
    }
    val tags = listOf("Смешарики", "За жизнь")
    DbProvider.Tags.batchInsert(tags) { tag ->
        this[name] = tag
        this[isPublic] = true
    }
    DbProvider.TagsOnQuotes.batchInsert(tags) { current ->
        this[quote] = 1
        this[tag] = DbProvider.Tags
            .slice(DbProvider.Tags.id)
            .select { name eq current }
            .firstOrNull()
            ?.let { it[DbProvider.Tags.id] } ?: run { throw IllegalArgumentException() }
    }
    DbProvider.TagsOnQuotes.insert { insertStatement ->
        insertStatement[quote] = 2
        insertStatement[tag] = DbProvider.Tags
            .slice(DbProvider.Tags.id)
            .select { name eq "Смешарики" }
            .firstOrNull()
            ?.let { it[DbProvider.Tags.id] } ?: run { throw IllegalArgumentException() }
    }
    DbProvider.LikesOnQuotes.insert { insertStatement ->
        insertStatement[quote] = 2
        insertStatement[user] = "a"
    }
    DbProvider.LikesOnQuotes.insert { insertStatement ->
        insertStatement[quote] = 1
        insertStatement[user] = "a"
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
