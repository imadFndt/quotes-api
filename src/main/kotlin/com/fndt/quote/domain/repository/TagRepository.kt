package com.fndt.quote.domain.repository

import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.dto.Tag

interface TagRepository {
    fun getTags(): List<Tag>
    fun add(tag: Tag): ID?
    fun remove(tagId: Int): Int
    fun update(tagId: Int, isPublic: Boolean? = null, name: String? = null): Tag?
    fun findTag(tagId: Int): Tag?
}
