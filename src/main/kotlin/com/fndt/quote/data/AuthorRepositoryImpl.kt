package com.fndt.quote.data

import com.fndt.quote.data.util.toAuthor
import com.fndt.quote.domain.dto.Author
import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.repository.AuthorRepository
import org.jetbrains.exposed.sql.*

class AuthorRepositoryImpl(databaseProvider: DatabaseProvider) : AuthorRepository {
    private val authorTable: DatabaseProvider.Authors by databaseProvider

    override fun get(): List<Author> {
        return authorTable.selectAll().map { it.toAuthor() }
    }

    override fun add(author: Author): ID {
        val authorExists = findById(author.id) != null
        return if (authorExists) update(author) else insert(author)
    }

    override fun remove(authorId: Int): Int {
        return authorTable.deleteWhere { authorTable.id eq authorId }
    }

    override fun findById(authorId: ID): Author? {
        return find(authorId = authorId)
    }

    override fun findByName(name: String): Author? {
        return find(name = name)
    }

    private fun find(authorId: ID? = null, name: String? = null): Author? {
        return authorTable.selectAll().apply {
            authorId?.let { andWhere { authorTable.id eq authorId } }
            name?.let { andWhere { authorTable.name eq name } }
        }.firstOrNull()?.toAuthor()
    }

    private fun insert(author: Author): ID {
        return authorTable.insert { insert ->
            insert[name] = author.name
        }[authorTable.id].value
    }

    private fun update(author: Author): ID {
        authorTable.update { update ->
            update[name] = author.name
        }
        return author.id
    }
}
