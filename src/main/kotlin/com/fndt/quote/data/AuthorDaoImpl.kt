package com.fndt.quote.data

import com.fndt.quote.data.util.toAuthor
import com.fndt.quote.domain.dao.AuthorDao
import com.fndt.quote.domain.dto.Author
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class AuthorDaoImpl(dbProvider: DatabaseProvider) : AuthorDao {
    private val authorTable: DatabaseProvider.Authors by dbProvider

    override fun addAuthor(name: String): Author? = transaction {
        val id = authorTable.insert { insert ->
            insert[this.name] = name
        }[authorTable.id].value
        findById(id)
    }

    override fun updateAuthor(authorId: Int, name: String): Author? = transaction {
        authorTable.update({ authorTable.id eq authorId }) { insert -> insert[this.name] = name }
        findById(authorId)
    }

    override fun removeAuthor(id: Int): Int = transaction {
        authorTable.deleteWhere { authorTable.id eq id }
    }

    override fun findById(id: Int): Author? {
        return authorTable.select { authorTable.id eq id }.firstOrNull()?.toAuthor()
    }
}
