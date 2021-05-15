package com.fndt.quote.domain.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class User(
    val id: ID = UNDEFINED,
    val name: String,
    val role: AuthRole = AuthRole.REGULAR,
    val blockedUntil: Long? = null,
    val avatarScheme: AvatarScheme = AvatarScheme.PANDA
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

    constructor(
        id: ID = UNDEFINED,
        name: String,
        password: String,
        role: AuthRole = AuthRole.REGULAR,
        blockedUntil: Long? = null,
        avatarScheme: AvatarScheme,
    ) : this(id, name, role, blockedUntil, avatarScheme) {
        hashedPassword = password
    }

    @Transient
    var hashedPassword: String = ""
}

val User.isBanned: Boolean get() = blockedUntil?.let { System.currentTimeMillis() < it } ?: false
fun User.getUnbanned(): User {
    return copy(blockedUntil = null)
}
