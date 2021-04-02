package com.fndt.quote.domain.manager

import com.fndt.quote.domain.dto.User

interface UserPermissionManager {
    fun isAuthAllowed(): Boolean
    fun isRegisterAllowed(): Boolean
    fun isAuthorized(user: User?): Boolean
    fun hasModeratorPermission(user: User?): Boolean
    fun hasAdminPermission(user: User?): Boolean
}
