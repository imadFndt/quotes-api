package com.fndt.quote.domain.repository

import com.fndt.quote.domain.dto.Like

interface LikeRepository {
    fun like(like: Like): Like?
    fun unlike(like: Like): Like?
    fun find(like: Like): Like?
    fun getLikesForQuote(quoteId: Int): List<Like>
}
