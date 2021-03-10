package com.fndt.quote.data

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database

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
        val date = long("date")
        val author = reference("author_id", Authors)
        val likes: Column<Int> = integer("likes")
    }

    object Authors : IntIdTable() {
        val name = varchar("name", 50)
    }

    object Users : IdTable<String>() {
        val name = varchar("username", 200)
        val hashedPassword = varchar("password_hash", 200)
        val role = byte("role")
        override val primaryKey: PrimaryKey get() = PrimaryKey(name)
        override val id: Column<EntityID<String>> = name.entityId()
    }
}
