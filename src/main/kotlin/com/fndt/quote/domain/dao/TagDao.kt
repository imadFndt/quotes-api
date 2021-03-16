package com.fndt.quote.domain.dao

interface TagDao {
    fun upsertTag(name: String? = null, isPublic: Boolean = false, tagId: Int? = null): Int
    fun remove(tagId: Int): Int
    fun addQuoteToTag(quoteId: Int, tagId: Int): Int
    fun insert(name: String): Int
    fun update(tagId: Int, isPublic: Boolean? = null, name: String? = null): Int
}
