package com.fndt.quote.domain.usecases.moderator

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class AddTagUseCase(
    private val tag: Tag,
    private val tagRepository: TagRepository,
    override val requestingUser: User?,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager
) : RequestUseCase<Unit>(requestManager) {
    override suspend fun makeRequest() {
        tagRepository.add(tag)
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasAddTagPermission(requestingUser)
    }
}
