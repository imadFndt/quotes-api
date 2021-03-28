package com.fndt.quote.domain.dto

import com.fndt.quote.domain.manager.UrlSchemeProvider
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class User(
    val id: ID = UNDEFINED,
    val name: String,
    val role: AuthRole = AuthRole.REGULAR,
    @SerialName("blocked_until") val blockedUntil: Long? = null,
    @SerialName("avatar_scheme") val avatarScheme: AvatarScheme = AvatarScheme.PANDA
) {
    constructor(
        id: ID = UNDEFINED,
        name: String,
        password: String,
        role: AuthRole = AuthRole.REGULAR,
        blockedUntil: Long? = null,
    ) : this(id, name, role, blockedUntil) {
        hashedPassword = password
    }

    @Required
    @SerialName("profile_url")
    val profileUrl = buildString {
        append(UrlSchemeProvider.scheme)
        append(if (avatarScheme == AvatarScheme.CUSTOM) id.toString() else avatarScheme.fileName)
    }

    @Transient
    var hashedPassword: String = ""
}

val User.isBanned: Boolean get() = blockedUntil?.let { System.currentTimeMillis() < it } ?: false
