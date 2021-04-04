package com.fndt.quote.domain.usecases.moderator

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.base.RequestUseCase

const val BAN_TIME = 24 * 60 * 60 * 1000

class BanReadOnlyUserUseCase(
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

    override suspend fun makeRequest() {
        val updatedUser = targetUser.copy(blockedUntil = System.currentTimeMillis() + BAN_TIME)
        userRepository.add(updatedUser)
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasModeratorPermission(user) && targetUser.role != AuthRole.ADMIN
    }
}
