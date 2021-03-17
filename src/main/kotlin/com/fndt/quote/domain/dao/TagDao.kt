package com.fndt.quote.domain.dao

import com.fndt.quote.domain.dto.Tag

interface TagDao {
    fun upsertTag(name: String? = null, isPublic: Boolean = false, tagId: Int? = null): Tag?
    fun remove(tagId: Int): Int
    fun insert(name: String): Tag?
    fun update(tagId: Int, isPublic: Boolean? = null, name: String? = null): Tag?
    fun getTags(): List<Tag>
    fun addQuoteToTag(quoteId: Int, tagId: Int): Tag?
    fun removeQuoteFromTag(quoteId: Int, tagId: Int): Int
}
