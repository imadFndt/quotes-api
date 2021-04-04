package com.fndt.quote.domain.usecases.admin

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.usecases.base.RequestUseCase

class ReviewTagUseCase(
    private val tagId: Int,
    private val decision: Boolean,
    private val tagRepository: TagRepository,
    override val requestingUser: User?,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : RequestUseCase<Unit>(requestManager) {

    override fun validate(user: User?) = permissionManager.hasAdminPermission(user)

    override suspend fun makeRequest() {
        val tag = tagRepository.findById(tagId) ?: throw IllegalStateException("Tag not found")
        if (decision) tagRepository.add(tag.approved()) else tagRepository.remove(tag)
    }
}

private fun Tag.approved(): Tag {
    return copy(isPublic = true)
}
