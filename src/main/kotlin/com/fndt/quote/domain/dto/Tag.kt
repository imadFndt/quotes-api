package com.fndt.quote.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val id: ID = UNDEFINED,
    val name: String,
    val isPublic: Boolean = false,
)
