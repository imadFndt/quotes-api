package com.fndt.quote.domain.usecases.moderator

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class AddTagUseCase(
    private val tagName: String,
    private val tagRepository: TagRepository,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : RequestUseCase<Unit>(requestManager) {
    override suspend fun makeRequest() {
        val tag = Tag(name = tagName)
        tagRepository.add(tag)
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasModeratorPermission(requestingUser)
    }
}
