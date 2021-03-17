package com.fndt.quote.domain.dao

import com.fndt.quote.domain.dto.Like

interface LikeDao {
    fun like(like: Like): Like?
    fun unlike(like: Like): Int
    fun find(like: Like): Like?
    fun getLikesForQuote(quoteId: Int): List<Like>
}
