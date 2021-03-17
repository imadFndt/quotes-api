package com.fndt.quote.domain.dto

data class User(
    val id: Int,
    val name: String,
    internal val hashedPassword: String,
    val role: AuthRole = AuthRole.REGULAR
)
