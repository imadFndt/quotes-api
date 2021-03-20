package com.fndt.quote.domain.manager.implementations

import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager

class PermissionManagerImpl : PermissionManager {
    override fun hasChangeRolePermission(user: User?): Boolean {
        return user?.role == AuthRole.ADMIN
    }

    override fun hasChangeTagVisibility(user: User?): Boolean {
        return user?.role == AuthRole.ADMIN
    }

    override fun hasGetCommentPermission(user: User?): Boolean {
        return user?.role != AuthRole.NOT_AUTHORIZED
    }

    override fun hasAddCommentPermission(user: User?): Boolean {
        return user?.role != AuthRole.NOT_AUTHORIZED
    }

    override fun hasAddTagPermission(user: User?): Boolean {
        return user?.role == AuthRole.ADMIN || user?.role == AuthRole.MODERATOR
    }

    override fun hasBanUserPermission(user: User?): Boolean {
        return user?.role == AuthRole.ADMIN || user?.role == AuthRole.MODERATOR
    }

    override fun hasSetQuoteVisibilityPermission(user: User?): Boolean {
        return user?.role == AuthRole.ADMIN || user?.role == AuthRole.MODERATOR
    }

    override fun hasAddQuotePermission(user: User?): Boolean {
        return user?.role != AuthRole.NOT_AUTHORIZED
    }

    override fun hasGetQuotesPermission(user: User?): Boolean {
        return user?.role != AuthRole.NOT_AUTHORIZED
    }

    override fun hasLikePermission(user: User?): Boolean {
        return user?.role != AuthRole.NOT_AUTHORIZED
    }

    override fun hasAuthPermission(user: User?): Boolean = true

    override fun hasRegisterPermission(user: User?): Boolean = true
}
