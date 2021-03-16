package com.fndt.quote.data

import com.fndt.quote.domain.dao.TagDao
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class TagDaoImpl(dbProvider: DatabaseProvider) : TagDao {
    private val tagsTable: DatabaseProvider.Tags by dbProvider
    private val tagsQuotesTable: DatabaseProvider.TagsOnQuotes by dbProvider

    override fun upsertTag(name: String?, isPublic: Boolean, tagId: Int?) = transaction {
        tagId?.let { tag ->
            update(tagId, isPublic, name)
            tag
        } ?: run {
            name ?: run { throw IllegalArgumentException("Insert with empty name") }
            insert(name)
        }
    }

    override fun insert(name: String) = transaction {
        tagsTable.insert { insert ->
            insert[tagsTable.name] = name
            insert[tagsTable.isPublic] = false
        }[tagsTable.id].value
    }

    override fun update(tagId: Int, isPublic: Boolean?, name: String?) = transaction {
        tagsTable.update({ tagsTable.id eq tagId }) { insert ->
            isPublic?.let { insert[tagsTable.isPublic] = it }
            name?.let { insert[tagsTable.name] = name }
        }
    }

    override fun remove(tagId: Int) = transaction {
        tagsTable.deleteWhere { tagsTable.id eq tagId }
    }

    override fun addQuoteToTag(quoteId: Int, tagId: Int) = transaction {
        tagsQuotesTable.insert { insert ->
            insert[tag] = tagId
            insert[quote] = quoteId
        }.execute(this) ?: OPERATION_FAILED
    }
}
