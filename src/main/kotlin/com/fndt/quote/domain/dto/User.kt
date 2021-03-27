package com.fndt.quote.domain.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class User(
    val id: ID = UNDEFINED,
    val name: String,
    val role: AuthRole = AuthRole.REGULAR,
    val blockedUntil: Long? = null,
    var profileUrl: String = DefaultAvatars.MONKEY.url
) {
    constructor(
        id: ID = UNDEFINED,
        name: String,
        password: String,
        role: AuthRole = AuthRole.REGULAR,
        blockedUntil: Long? = null
    ) : this(id, name, role, blockedUntil) {
        hashedPassword = password
    }

    @Transient
    var hashedPassword: String = ""
}
