package com.fndt.quote.data

import com.fndt.quote.data.util.populateDb
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.AvatarScheme
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import kotlin.reflect.KProperty

object DatabaseProvider {
    val initDb by lazy {
        // LOCAL IS jdbc:h2:mem:
        // TODO CHANGE INIT LOGIC
        Database.connect(
            "jdbc:h2:file:./webapps/quotyv44;DB_CLOSE_DELAY=-1",
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
            Authors::javaClass.get().kotlin -> Authors as T
            RandomQuotes::javaClass.get().kotlin -> RandomQuotes as T
            else -> throw IllegalArgumentException()
        }
    }

    abstract class AccessLimitableIntIdTable(tableName: String) : IntIdTable(tableName) {
        abstract val isPublic: Column<Boolean>
    }

    object Quotes : AccessLimitableIntIdTable("Quotes") {
        val body = varchar("body", 200)
        val createdAt = long("created_at")
        val user = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
        val author = reference("author", Authors)
        override val isPublic = bool("is_public")
    }

    object Comments : IntIdTable() {
        val body = varchar("body", 300)
        val quoteId = reference("quote_id", Quotes, onDelete = ReferenceOption.CASCADE)
        val createdAt = long("date")
        val user = reference("user", Users, onDelete = ReferenceOption.CASCADE)
    }

    object LikesOnQuotes : Table() {
        val user = reference("user", Users, onDelete = ReferenceOption.CASCADE)
        val quote = reference("quote", Quotes, onDelete = ReferenceOption.CASCADE)
    }

    object Tags : AccessLimitableIntIdTable("Tags") {
        val name = varchar("name", 50)
        override val isPublic = bool("is_public")
    }

    object TagsOnQuotes : Table() {
        val quote = reference("quote", Quotes, onDelete = ReferenceOption.CASCADE)
        val tag = reference("tag", Tags, onDelete = ReferenceOption.CASCADE)
    }

    // Compose PK is not supported, but name should be unique
    object Users : IntIdTable() {
        val name = varchar("username", 50)
        val hashedPassword = varchar("password_hash", 50)
        val role = enumeration("user_role", AuthRole::class)
        val blockedUntil = long("blocked_until").nullable()
        val avatarScheme = enumeration("avatar", AvatarScheme::class)
    }

    object RandomQuotes : Table() {
        val quote = reference("quote", Quotes, onDelete = ReferenceOption.CASCADE)
        val user = reference("user", Users, onDelete = ReferenceOption.CASCADE)
        val day = integer("day")
        override val primaryKey: PrimaryKey get() = PrimaryKey(user)
    }

    object Authors : IntIdTable() {
        val name = varchar("name", 30)
    }
}
