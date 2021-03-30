package com.fndt.quote.domain.manager

import com.fndt.quote.domain.dto.AvatarScheme
import com.fndt.quote.domain.dto.User

object UrlSchemeProvider {
    lateinit var scheme: String
        private set

    fun initScheme(host: String) {
        scheme = "http://$host/images/"
    }

    fun getUrlFor(user: User) = buildString {
        append(scheme)
        append(if (user.avatarScheme == AvatarScheme.CUSTOM) user.id.toString() else user.avatarScheme.fileName)
    }
}
