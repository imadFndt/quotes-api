package com.fndt.quote.domain.repository.base

import com.fndt.quote.domain.dto.ID

interface SimpleRepository<T> : BaseRepository {
    fun add(item: T): ID
    fun remove(item: T)
    fun findById(itemId: ID): T?
}
