package com.fndt.quote.data

import com.fndt.quote.domain.dto.Author
import com.fndt.quote.domain.dto.Quote
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

private fun Database.populateDb() = transaction {
    SchemaUtils.create(DbProvider.Quotes, DbProvider.Authors)
    DbProvider.Authors.deleteAll()
    DbProvider.Quotes.deleteAll()
    DbProvider.Authors.batchInsert(authorList) { author ->
        this[DbProvider.Authors.name] = author.name
    }
    DbProvider.Quotes.batchInsert(quotesList) { quote ->
        this[DbProvider.Quotes.body] = quote.body
        this[DbProvider.Quotes.date] = System.currentTimeMillis()
        this[DbProvider.Quotes.likes] = quote.likes
        val authorId = DbProvider.Authors
            .slice(DbProvider.Authors.id)
            .select { DbProvider.Authors.name eq quote.author.name }
            .limit(1)
            .firstOrNull()
            ?.let { it[DbProvider.Authors.id] } ?: run { throw IllegalArgumentException() }
        this[DbProvider.Quotes.author] = authorId
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
        author = Author(0, "Бараш")
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
