package com.fndt.quote.data

import com.fndt.quote.data.util.toAuthor
import com.fndt.quote.domain.dao.AuthorDao
import com.fndt.quote.domain.dto.Author
import org.jetbrains.exposed.sql.select

class AuthorDaoImpl(dbProvider: DatabaseProvider) : AuthorDao {
    private val authorTable: DatabaseProvider.Authors by dbProvider

    override fun findById(id: Int): Author? {
        return authorTable.select { authorTable.id eq id }.firstOrNull()?.toAuthor()
    }
}
