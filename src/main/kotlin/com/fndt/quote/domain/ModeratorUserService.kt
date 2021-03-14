package com.fndt.quote.domain

import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Tag

interface ModeratorUserService : RegularUserService {
    suspend fun banUserTemporary(userId: Int, newRole: AuthRole, time: Int): Boolean
    suspend fun setQuoteVisibility(quoteId: Int, isPublic: Boolean): Boolean
    suspend fun addTagForModeration(tag: Tag): Boolean
}
