package com.fndt.quote.controllers.factory

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.UseCase
import com.fndt.quote.domain.usecases.admin.ChangeRoleUseCase
import com.fndt.quote.domain.usecases.admin.PermanentBanUseCase
import com.fndt.quote.domain.usecases.admin.ReviewTagUseCase

class AdminUseCaseFactory(
    private val userRepository: UserRepository,
    private val tagRepository: TagRepository,
    private val permissionManager: UserPermissionManager,
    private val requestManager: RequestManager
) {
    fun getApproveTagUseCase(tagId: Int, decision: Boolean, requestingUser: User): UseCase<Unit> {
        return ReviewTagUseCase(
            tagId,
            decision,
            tagRepository,
            requestingUser,
            permissionManager,
            requestManager
        )
    }

    fun getChangeRoleUseCase(userId: Int, newRole: AuthRole, requestingUser: User): UseCase<Unit> {
        return ChangeRoleUseCase(userId, newRole, userRepository, requestingUser, permissionManager, requestManager)
    }

    fun getPermanentBanUseCase(userId: Int, requestingUser: User): UseCase<Unit> {
        return PermanentBanUseCase(userId, userRepository, requestingUser, permissionManager, requestManager)
    }
}
