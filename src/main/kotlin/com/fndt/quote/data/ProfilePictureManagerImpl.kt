package com.fndt.quote.data

import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.ProfilePictureManager
import java.io.File

class ProfilePictureManagerImpl : ProfilePictureManager {
    override val imagesScheme: String = ".files/images"

    override fun saveProfilePicture(file: File, user: User) {
        file.copyTo(getProfilePictureFile(user), true)
        file.delete()
    }

    override fun deleteProfilePicture(user: User) {
        getProfilePictureFile(user).delete()
    }

    override fun dismissFile(file: File) {
        file.delete()
    }

    private fun getProfilePictureFile(user: User): File {
        return File(imagesScheme, "${user.name}.$acceptedExtension")
    }
}
