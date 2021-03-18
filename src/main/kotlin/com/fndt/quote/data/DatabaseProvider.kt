package com.fndt.quote.data

import com.fndt.quote.data.util.populateDb
import com.fndt.quote.domain.dto.AuthRole
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import kotlin.reflect.KProperty

object DatabaseProvider {
    val initDb by lazy {
        // LOCAL IS jdbc:h2:mem:
        Database.connect(
            "jdbc:h2:file:/Users/imadfndt/IdeaProjects/quotes-test/src/main/resources/db/quotes;DB_CLOSE_DELAY=-1",
            "org.h2.Driver",
            "root",
            ""
        ).apply {
            populateDb()
        }
    }

    inline operator fun <reified T : Table> getValue(any: Any, property: KProperty<*>): T {
        return when (T::class) {
            Quotes::javaClass.get().kotlin -> Quotes as T
            Tags::javaClass.get().kotlin -> Tags as T
            Comments::javaClass.get().kotlin -> Comments as T
            LikesOnQuotes::javaClass.get().kotlin -> LikesOnQuotes as T
            TagsOnQuotes::javaClass.get().kotlin -> TagsOnQuotes as T
            Users::javaClass.get().kotlin -> Users as T
            else -> throw IllegalArgumentException()
        }
    }

    abstract class AccessLimitableIntIdTable : IntIdTable() {
        abstract val isPublic: Column<Boolean>
    }

    object Quotes : DatabaseProvider.AccessLimitableIntIdTable() {
        val body = varchar("body", 200)
        val createdAt = long("date")
        val user = reference("user_id", Users)
        override val isPublic = bool("is_public")
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

    object Tags : DatabaseProvider.AccessLimitableIntIdTable() {
        val name = varchar("name", 50)
        override val isPublic = bool("is_public")
    }

    object TagsOnQuotes : Table() {
        val quote = reference("quote", Quotes)
        val tag = reference("tag", Tags)
    }

    // Compose PK is not supported, but name should be unique
    object Users : IntIdTable() {
        val name = varchar("username", 200)
        val hashedPassword = varchar("password_hash", 200)
        val role = enumeration("role", AuthRole::class)
        val blockedUntil = long("blocked_until").nullable()
    }
}
