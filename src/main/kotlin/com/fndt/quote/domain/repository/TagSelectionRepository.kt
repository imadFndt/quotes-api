package com.fndt.quote.domain.repository

import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.Tag

interface TagSelectionRepository {
    fun add(quote: Quote, tag: Tag)
    fun remove(quote: Quote, tag: Tag)
    fun getSelectionByTag(tag: Tag): List<Quote>
}
