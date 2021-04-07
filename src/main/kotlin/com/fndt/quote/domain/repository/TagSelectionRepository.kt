package com.fndt.quote.domain.repository

import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.repository.base.BaseRepository

interface TagSelectionRepository : BaseRepository {
    fun get(): Map<ID, ID>
    fun add(quote: Quote, tag: Tag)
    fun remove(quote: Quote, tag: Tag)
}
