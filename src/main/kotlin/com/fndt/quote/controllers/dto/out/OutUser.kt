package com.fndt.quote.controllers.dto.out

import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UrlSchemeProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OutUser(
    val id: Int,
    val name: String,
    val role: AuthRole,
    @SerialName("blocked_until") val blockedUntil: Long?,
    @SerialName("avatar_url") val avatarUrl: String
)

fun User.toOutUser(urlSchemeProvider: UrlSchemeProvider): OutUser {
    return OutUser(id, name, role, blockedUntil, urlSchemeProvider.getUrlFor(this))
}
