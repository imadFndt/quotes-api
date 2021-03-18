package com.fndt.quote.domain.services

import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User

interface ModeratorUserService : RegularUserService {
    suspend fun setBanState(userId: Int, time: Int?): Boolean
    suspend fun setQuoteVisibility(quoteId: Int, isPublic: Boolean): Boolean
    suspend fun addTagForModeration(tag: Tag): Boolean
    suspend fun getUserById(userId: Int): User
}
