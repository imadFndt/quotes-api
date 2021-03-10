package com.fndt.quote.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String,
    val hashedPassword: String,
    val role: AuthRole
)
