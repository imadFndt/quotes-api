package com.fndt.quote.domain.repository

import com.fndt.quote.domain.dto.Like

interface LikeRepository {
    fun getLikesForQuote(quoteId: Int): List<Like>
    fun add(like: Like)
    fun remove(like: Like)
    fun find(like: Like): Like?
}
