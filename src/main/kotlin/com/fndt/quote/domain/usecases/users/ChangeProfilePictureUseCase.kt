package com.fndt.quote.domain.usecases.users

import com.fndt.quote.domain.dto.AvatarScheme
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.ProfilePictureManager
import com.fndt.quote.domain.manager.RequestManager
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.base.RequestUseCase
import java.io.File

class ChangeProfilePictureUseCase(
    private val newPicture: File,
    override val requestingUser: User,
    private val pictureManager: ProfilePictureManager,
    private val userRepository: UserRepository,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : RequestUseCase<Unit>(requestManager) {

    override fun validate(user: User?): Boolean {
        return permissionManager.isAuthorized(user)
    }

    override suspend fun makeRequest() {
        userRepository.findUserByParams(userId = requestingUser.id) ?: throw IllegalStateException("User not found")
        val (width, height) = pictureManager.getResolution(newPicture)
        with(pictureManager) {
            val deleteCondition = newPicture.extension != acceptedExtension &&
                (width > acceptedWidth || height > acceptedHeight)
            if (deleteCondition) {
                dismissFile(newPicture)
                throw IllegalStateException("Bad file")
            } else {
                saveProfilePicture(newPicture, requestingUser)
            }
        }
        if (requestingUser.avatarScheme != AvatarScheme.CUSTOM) {
            userRepository.add(requestingUser.copy(avatarScheme = AvatarScheme.CUSTOM))
        }
    }
}
