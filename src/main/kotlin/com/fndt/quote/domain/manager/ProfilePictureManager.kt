package com.fndt.quote.domain.manager

import com.fndt.quote.domain.dto.User
import java.io.File

interface ProfilePictureManager {
    val imagesScheme: String
    val acceptedExtension: String
        get() = "jpg"

    fun saveProfilePicture(file: File, user: User)
    fun deleteProfilePicture(user: User)

    fun dismissFile(file: File)
}
