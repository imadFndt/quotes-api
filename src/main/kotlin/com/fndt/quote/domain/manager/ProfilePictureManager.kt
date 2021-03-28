package com.fndt.quote.domain.manager

import com.fndt.quote.domain.dto.User
import java.io.File

interface ProfilePictureManager {
    val imagesScheme: String
    val acceptedExtension: String
        get() = "jpeg"
    val acceptedHeight get() = 500
    val acceptedWidth get() = 500

    fun saveProfilePicture(file: File, user: User)
    fun deleteProfilePicture(user: User)
    fun dismissFile(file: File)

    suspend fun getResolution(file: File): Pair<Int, Int>
}
