package com.fndt.quote.domain.usecases.get

import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.Access
import com.fndt.quote.domain.manager.RequestManager
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.usecases.base.RequestUseCase

class GetTagsUseCase(
    private var access: Access = Access.PUBLIC,
    private val tagRepository: TagRepository,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : RequestUseCase<List<Tag>>(requestManager) {

    private val User.isRegularUsingPrivateData
        get() = role == AuthRole.REGULAR && (access == Access.PRIVATE || access == Access.ALL)

    override fun validate(user: User?): Boolean {
        access = if (user?.role in listOf(AuthRole.MODERATOR, AuthRole.ADMIN)) Access.ALL else access
        return permissionManager.isAuthorized(user) && user?.isRegularUsingPrivateData == false
    }

    override suspend fun makeRequest(): List<Tag> {
        return tagRepository.findByAccess(access)
    }
}
