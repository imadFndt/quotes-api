package com.fndt.quote.domain.services

import com.fndt.quote.domain.dto.AuthRole

interface AdminUserService : ModeratorUserService {
    suspend fun setTagVisibility(tagId: Int, isPublic: Boolean): Boolean
    suspend fun changeRole(userId: Int, newRole: AuthRole, oldRole: AuthRole): Boolean
}
