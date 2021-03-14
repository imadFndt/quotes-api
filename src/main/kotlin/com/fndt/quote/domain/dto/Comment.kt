package com.fndt.quote.domain.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: Int,
    val body: String,
    @SerialName("quote_id") val quoteId: Int,
    val date: Long,
    val user: String
)
