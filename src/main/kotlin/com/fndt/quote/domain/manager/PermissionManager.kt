package com.fndt.quote.domain.manager

import com.fndt.quote.domain.dto.User

@Deprecated("Deprecated to grouped parameters")
interface PermissionManager {
    fun hasChangeRolePermission(user: User?): Boolean
    fun hasApproveTagVisibility(user: User?): Boolean

    fun hasGetCommentPermission(user: User?): Boolean
    fun hasAddCommentPermission(user: User?): Boolean

    fun hasAddTagPermission(user: User?): Boolean
    fun hasBanUserPermission(user: User?): Boolean
    fun hasSetQuoteVisibilityPermission(user: User?): Boolean

    fun hasAddQuotePermission(user: User?): Boolean
    fun hasGetQuotesPermission(user: User?): Boolean
    fun hasLikePermission(user: User?): Boolean

    fun hasAuthPermission(user: User?): Boolean
    fun hasRegisterPermission(user: User?): Boolean

    fun hasSearchPermission(user: User?): Boolean
    fun hasPopularsPermission(user: User?): Boolean
    fun hasTagSelectionsPermission(user: User?): Boolean

    fun hasChangeProfilePicturePermission(user: User?): Boolean
}
