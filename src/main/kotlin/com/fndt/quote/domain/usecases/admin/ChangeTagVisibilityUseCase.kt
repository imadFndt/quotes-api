package com.fndt.quote.domain.usecases.admin

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class ChangeTagVisibilityUseCase(
    private val tagId: Int,
    private val isPublic: Boolean,
    private val tagRepository: TagRepository,
    override val requestingUser: User?,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager
) : RequestUseCase<Tag>(requestManager) {
    override suspend fun makeRequest(): Tag {
        return tagRepository.update(tagId = tagId, isPublic = isPublic)
            ?: throw IllegalStateException("Tag visibility failed")
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasChangeTagVisibility(user)
    }
}
