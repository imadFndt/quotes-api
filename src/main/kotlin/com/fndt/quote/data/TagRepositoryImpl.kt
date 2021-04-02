package com.fndt.quote.data

import com.fndt.quote.data.util.toTag
import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.repository.TagRepository
import org.jetbrains.exposed.sql.*

class TagRepositoryImpl(dbProvider: DatabaseProvider) : TagRepository {
    private val tagsTable: DatabaseProvider.Tags by dbProvider

    override fun get(): List<Tag> {
        return tagsTable.selectAll().map { it.toTag() }
    }

    override fun add(tag: Tag): ID {
        val tagExists = findById(tag.id) != null
        return if (tagExists) update(tag) else insert(tag)
    }

    override fun remove(tagId: Int): Int {
        return tagsTable.deleteWhere { tagsTable.id eq tagId }
    }

    private fun insert(tag: Tag): ID {
        return tagsTable.insert { insert ->
            insert[name] = tag.name
            insert[isPublic] = tag.isPublic
        }[tagsTable.id].value
    }

    private fun update(tag: Tag): ID {
        tagsTable.update({ tagsTable.id eq tag.id }) { update ->
            update[name] = tag.name
            update[isPublic] = tag.isPublic
        }
        return tag.id
    }

    override fun findById(id: Int): Tag? {
        return tagsTable.select { tagsTable.id eq id }.firstOrNull()?.toTag()
    }
}
