package com.fndt.quote.domain.usecases.users

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.ProfilePictureManager
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.RequestUseCase
import java.io.File

class ChangeProfilePictureUseCase(
    private val newAvatar: File,
    override val requestingUser: User,
    private val pictureManager: ProfilePictureManager,
    private val userRepository: UserRepository,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : RequestUseCase<Unit>(requestManager) {

    override suspend fun makeRequest() {
        userRepository.findUserByParams(userId = requestingUser.id) ?: throw IllegalStateException("User not found")
        // TODO CATCH BAD PICTURE
        with(pictureManager) {
            val deleteCondition = newAvatar.extension != pictureManager.acceptedExtension
            if (deleteCondition) dismissFile(newAvatar) else saveProfilePicture(newAvatar, requestingUser)
        }
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.isAuthorized(user)
    }
}
