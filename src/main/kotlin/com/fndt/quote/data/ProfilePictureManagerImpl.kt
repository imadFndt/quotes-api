package com.fndt.quote.data

import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.ProfilePictureManager
import java.io.File
import javax.imageio.ImageIO

class ProfilePictureManagerImpl(override val imagesScheme: String) : ProfilePictureManager {

    override fun saveProfilePicture(file: File, user: User) {
        val a = file.copyTo(getProfilePictureFile(user), true)
        println(a.exists())
        file.delete()
    }

    override fun deleteProfilePicture(user: User) {
        getProfilePictureFile(user).delete()
    }

    override fun dismissFile(file: File) {
        file.delete()
    }

    override suspend fun getResolution(file: File): Pair<Int, Int> = with(ImageIO.read(file)) { width to height }

    private fun getProfilePictureFile(user: User): File {
        return File(imagesScheme, "${user.id}.$acceptedExtension")
    }
}
