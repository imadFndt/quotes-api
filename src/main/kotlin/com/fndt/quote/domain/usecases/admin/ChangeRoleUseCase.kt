package com.fndt.quote.domain.usecases.admin

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.base.RequestUseCase

class ChangeRoleUseCase(
    private val userId: Int,
    private val newRole: AuthRole,
    private val userRepository: UserRepository,
    override val requestingUser: User?,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager,
) : RequestUseCase<Unit>(requestManager) {

    override fun validate(user: User?) = permissionManager.hasAdminPermission(user)

    override suspend fun makeRequest() {
        val user = userRepository.findUserByParams(userId = userId) ?: throw IllegalStateException("User not found")
        val updatedUser = user.updateRole(newRole)
        userRepository.add(updatedUser)
    }

    private fun User.updateRole(newRole: AuthRole): User {
        return copy(role = newRole)
    }
}
