package com.fndt.quote.domain.manager

import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.UseCase

interface AdminUseCaseManager {
    fun changeRoleUseCase(
        userId: Int,
        newRole: AuthRole,
        userRepository: UserRepository,
    ): UseCase<User>

    fun changeTagVisibilityUseCase(
        tagId: Int,
        isPublic: Boolean,
        tagRepository: TagRepository,
    ): UseCase<Tag>
}
