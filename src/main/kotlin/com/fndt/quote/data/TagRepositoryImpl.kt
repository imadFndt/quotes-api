package com.fndt.quote.data

import com.fndt.quote.data.util.toTag
import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.repository.TagRepository
import org.jetbrains.exposed.sql.*

class TagRepositoryImpl(dbProvider: DatabaseProvider) : TagRepository {
    private val tagsTable: DatabaseProvider.Tags by dbProvider
    private val tagsQuotesTable: DatabaseProvider.TagsOnQuotes by dbProvider
    private val quotesTable: DatabaseProvider.TagsOnQuotes by dbProvider

    override fun add(tag: Tag): ID {
        return tagsTable.insert { insert ->
            insert[name] = name
            insert[isPublic] = false
        }[tagsTable.id].value
    }

    override fun update(tagId: Int, isPublic: Boolean?, name: String?): Tag? {
        return tagsTable.update({ tagsTable.id eq tagId }) { insert ->
            isPublic?.let { insert[DatabaseProvider.Tags.isPublic] = it }
            name?.let { insert[DatabaseProvider.Tags.name] = name }
        }.let {
            findById(it)
        }
    }

    override fun getTags(): List<Tag> {
        return tagsTable.selectAll().map { it.toTag() }
    }

    override fun remove(tagId: Int): Int {
        return tagsTable.deleteWhere { tagsTable.id eq tagId }
    }

    override fun findTag(tagId: Int): Tag? {
        return tagsTable.select { tagsTable.id eq tagId }.firstOrNull()?.toTag()
    }

    private fun findById(id: Int): Tag? {
        return tagsTable.select { tagsTable.id eq id }.firstOrNull()?.toTag()
    }
}
