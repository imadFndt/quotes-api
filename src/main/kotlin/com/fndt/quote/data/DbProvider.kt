package com.fndt.quote.data

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table

object DbProvider {
    val initDb by lazy {
        Database.connect(
            "jdbc:h2:file:/Users/imadfndt/IdeaProjects/quotes-test/src/main/resources/db/quotes;DB_CLOSE_DELAY=-1",
            "org.h2.Driver",
            "root",
            ""
        ).apply {
            populateDb()
        }
    }

    object Quotes : IntIdTable() {
        val body = varchar("body", 200)
        val createdAt = long("date")
        val author = reference("author_id", Authors)
        val isPublic = bool("is_public")
    }

    object Authors : IntIdTable() {
        val name = varchar("name", 50)
    }

    object Comments : IntIdTable() {
        val body = varchar("body", 300)
        val quoteId = reference("quote_id", Quotes)
        val createdAt = long("date")
        val user = reference("user", Users)
    }

    object LikesOnQuotes : Table() {
        val user = reference("user", Users)
        val quote = reference("quote", Quotes)
    }

    object Tags : IntIdTable() {
        val name = varchar("name", 50)
        val isPublic = bool("is_public")
    }

    object TagsOnQuotes : Table() {
        val quote = reference("quote", Quotes)
        val tag = reference("tag", Tags)
    }

    // todo intid table
    object Users : IdTable<String>() {
        val name = varchar("username", 200)
        val hashedPassword = varchar("password_hash", 200)
        val role = byte("role")
        override val id = name.entityId()
    }
}
