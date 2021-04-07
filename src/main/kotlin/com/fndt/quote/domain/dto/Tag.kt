package com.fndt.quote.domain.dto

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val id: ID = UNDEFINED,
    val name: String,
    @Required
    @SerialName("is_public")
    val isPublic: Boolean = false,
)
