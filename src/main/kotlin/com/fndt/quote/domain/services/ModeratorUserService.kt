package com.fndt.quote.domain.services

import com.fndt.quote.domain.dto.Tag

interface ModeratorUserService : RegularUserService {
    suspend fun banUser(userId: Int, time: Int): Boolean
    suspend fun setQuoteVisibility(quoteId: Int, isPublic: Boolean): Boolean
    suspend fun addTagForModeration(tag: Tag): Boolean
}
