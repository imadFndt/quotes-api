package com.fndt.quote.domain.usecases.admin

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class ChangeRoleUseCase(
    private val userId: Int,
    private val newRole: AuthRole,
    private val userRepository: UserRepository,
    override val requestingUser: User?,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager,
) : RequestUseCase<Boolean>(requestManager) {
    override suspend fun makeRequest(): Boolean = userRepository.update(userId, role = newRole) != null

    override fun validate(user: User?) = permissionManager.hasChangeRolePermission(user)
}
