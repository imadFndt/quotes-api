package com.fndt.quote.domain.repository

import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.dto.Tag

interface TagRepository {
    fun get(): List<Tag>
    fun add(tag: Tag): ID
    fun remove(tag: Tag): Int
    fun findById(id: ID): Tag?
}
