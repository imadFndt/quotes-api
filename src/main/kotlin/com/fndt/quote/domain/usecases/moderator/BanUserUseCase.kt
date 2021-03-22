package com.fndt.quote.domain.usecases.moderator

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.RequestUseCase

const val BAN_TIME = 24 * 60 * 60 * 1000

class BanUserUseCase(
    private val userId: Int,
    private val userRepository: UserRepository,
    override val requestingUser: User,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager
) : RequestUseCase<Unit>(requestManager) {

    override suspend fun makeRequest() {
        userRepository.findUserByParams(userId) ?: throw IllegalStateException("User not found")
        userRepository.update(
            time = System.currentTimeMillis() + BAN_TIME ?: run { null },
            userId = userId
        ) ?: throw IllegalStateException("Update failed")
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasBanUserPermission(user)
    }
}
