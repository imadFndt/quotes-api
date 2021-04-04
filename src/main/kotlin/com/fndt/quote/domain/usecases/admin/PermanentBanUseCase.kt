package com.fndt.quote.domain.usecases.admin

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class PermanentBanUseCase(
    private val userId: Int,
    private val userRepository: UserRepository,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : RequestUseCase<Unit>(requestManager) {

    lateinit var targetUser: User

    override fun onStartRequest() {
        targetUser = userRepository.findUserByParams(userId) ?: throw IllegalStateException("User not found")
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasAdminPermission(user) && targetUser.role != AuthRole.ADMIN
    }

    override suspend fun makeRequest() {
        userRepository.remove(targetUser.id)
    }
}
