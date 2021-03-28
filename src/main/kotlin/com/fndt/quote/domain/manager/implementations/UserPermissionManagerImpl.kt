package com.fndt.quote.domain.manager.implementations

import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager

class UserPermissionManagerImpl : UserPermissionManager {

    override fun isAuthAllowed() = true

    override fun isRegisterAllowed() = true

    override fun isAuthorized(user: User?) = user.checkRole { it.role != AuthRole.NOT_AUTHORIZED }

    override fun hasModeratorPermission(user: User?): Boolean {
        return user.checkRole { it.role == AuthRole.MODERATOR || it.role == AuthRole.ADMIN }
    }

    override fun hasAdminPermission(user: User?) = user.checkRole { it.role == AuthRole.ADMIN }

    private fun User?.checkRole(block: (User) -> Boolean) = this?.let { block(it) } ?: false
}
