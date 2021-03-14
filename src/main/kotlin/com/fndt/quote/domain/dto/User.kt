package com.fndt.quote.domain.dto

data class User(
    val name: String,
    val hashedPassword: String,
    val role: AuthRole
)
