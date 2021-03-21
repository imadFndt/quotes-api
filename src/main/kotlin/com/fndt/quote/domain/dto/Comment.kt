package com.fndt.quote.domain.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: ID = UNDEFINED,
    val body: String,
    @SerialName("quote_id") val quoteId: Int,
    @SerialName("created_at") val createdAt: Long,
    val user: User
)
