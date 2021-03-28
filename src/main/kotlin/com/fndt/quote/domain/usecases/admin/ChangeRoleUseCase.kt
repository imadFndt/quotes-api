package com.fndt.quote.domain.usecases.admin

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class ChangeRoleUseCase(
    private val userId: Int,
    private val newRole: AuthRole,
    private val userRepository: UserRepository,
    override val requestingUser: User?,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager,
) : RequestUseCase<Unit>(requestManager) {
    override suspend fun makeRequest() {
        userRepository.update(userId, role = newRole)
    }

    override fun validate(user: User?) = permissionManager.hasAdminPermission(user)
}
