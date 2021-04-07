package com.fndt.quote.rest

import com.fndt.quote.domain.dto.AvatarScheme
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.SUPPORTED_EXTENSION

object UrlSchemeProvider {
    lateinit var scheme: String
        private set

    fun initScheme(host: String) {
        scheme = "http://$host/images/"
    }

    fun getUrlFor(user: User) = buildString {
        append(scheme)
        append(user.toProfilePictureName())
    }
}

private fun User.toProfilePictureName(): String = when (avatarScheme) {
    AvatarScheme.PANDA -> "panda.$SUPPORTED_EXTENSION"
    AvatarScheme.CUSTOM -> "$id.$SUPPORTED_EXTENSION"
}
