package com.fndt.quote.domain.dao

import com.fndt.quote.domain.dto.Like

interface LikeDao {
    fun like(like: Like): Int
    fun unlike(like: Like): Int
    fun find(like: Like): Like?
}
