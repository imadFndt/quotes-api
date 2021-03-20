package com.fndt.quote.data

import com.fndt.quote.data.util.toTag
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.dto.Tag
import org.jetbrains.exposed.sql.*

class TagRepositoryImpl(dbProvider: DatabaseProvider) : TagRepository {
    private val tagsTable: DatabaseProvider.Tags by dbProvider
    private val tagsQuotesTable: DatabaseProvider.TagsOnQuotes by dbProvider
    private val quotesTable: DatabaseProvider.TagsOnQuotes by dbProvider

    override fun insert(name: String): Tag? {
        return tagsTable.insert { insert ->
            insert[tagsTable.name] = name
            insert[tagsTable.isPublic] = false
        }[tagsTable.id].value.let {
            findById(it)
        }
    }

    override fun update(tagId: Int, isPublic: Boolean?, name: String?): Tag? {
        return tagsTable.update({ tagsTable.id eq tagId }) { insert ->
            isPublic?.let { insert[tagsTable.isPublic] = it }
            name?.let { insert[tagsTable.name] = name }
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

    override fun addQuoteToTag(quoteId: Int, tagId: Int): Tag? {
        return tagsQuotesTable.insert { insert ->
            insert[tag] = tagId
            insert[quote] = quoteId
        }[tagsQuotesTable.tag].value.let {
            findById(it)
        }
    }

    override fun removeQuoteFromTag(quoteId: Int, tagId: Int): Int {
        return tagsQuotesTable.deleteWhere { (tagsQuotesTable.quote eq quoteId) and (tagsQuotesTable.tag eq tagId) }
    }

    override fun findTag(tagId: Int): Tag? {
        return tagsTable.select { tagsTable.id eq tagId }.firstOrNull()?.toTag()
    }

    private fun findById(id: Int): Tag? {
        return tagsTable.select { tagsTable.id eq id }.firstOrNull()?.toTag()
    }
}
