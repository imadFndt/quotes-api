package com.fndt.quote.domain.repository

import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.filter.Access
import com.fndt.quote.domain.repository.base.SimpleRepository

interface TagRepository : SimpleRepository<Tag> {
    fun get(): List<Tag>
    fun findByAccess(access: Access): List<Tag>
}
