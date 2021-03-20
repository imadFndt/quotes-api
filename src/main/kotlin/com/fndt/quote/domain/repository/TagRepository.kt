package com.fndt.quote.domain.repository

import com.fndt.quote.domain.dto.Tag

interface TagRepository {
    fun remove(tagId: Int): Int
    fun insert(name: String): Tag?
    fun update(tagId: Int, isPublic: Boolean? = null, name: String? = null): Tag?
    fun getTags(): List<Tag>
    fun addQuoteToTag(quoteId: Int, tagId: Int): Tag?
    fun removeQuoteFromTag(quoteId: Int, tagId: Int): Int
    fun findTag(tagId: Int): Tag?
}
