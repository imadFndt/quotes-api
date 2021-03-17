package com.fndt.quote.data

import com.fndt.quote.data.util.toTag
import com.fndt.quote.domain.dao.TagDao
import com.fndt.quote.domain.dto.Tag
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class TagDaoImpl(dbProvider: DatabaseProvider) : TagDao {
    private val tagsTable: DatabaseProvider.Tags by dbProvider
    private val tagsQuotesTable: DatabaseProvider.TagsOnQuotes by dbProvider
    private val quotesTable: DatabaseProvider.TagsOnQuotes by dbProvider

    override fun upsertTag(name: String?, isPublic: Boolean, tagId: Int?) = transaction {
        tagId?.let { tag ->
            update(tagId, isPublic, name)
            findById(tag)
        } ?: run {
            name ?: run { throw IllegalArgumentException("Insert with empty name") }
            insert(name)
        }
    }

    override fun insert(name: String) = transaction {
        findById(
            tagsTable.insert { insert ->
                insert[tagsTable.name] = name
                insert[tagsTable.isPublic] = false
            }[tagsTable.id].value
        )
    }

    override fun update(tagId: Int, isPublic: Boolean?, name: String?) = transaction {
        tagsTable.update({ tagsTable.id eq tagId }) { insert ->
            isPublic?.let { insert[tagsTable.isPublic] = it }
            name?.let { insert[tagsTable.name] = name }
        }
        findById(tagId)
    }

    override fun getTags(): List<Tag> = transaction {
        tagsTable.selectAll().map { it.toTag() }
    }

    override fun remove(tagId: Int) = transaction {
        tagsTable.deleteWhere { tagsTable.id eq tagId }
    }

    override fun addQuoteToTag(quoteId: Int, tagId: Int) = transaction {
        val id = tagsQuotesTable.insert { insert ->
            insert[tag] = tagId
            insert[quote] = quoteId
        }[tagsQuotesTable.tag].value
        findById(id)
    }

    private fun findById(id: Int) = tagsTable.select { tagsTable.id eq id }.firstOrNull()?.toTag()
}
