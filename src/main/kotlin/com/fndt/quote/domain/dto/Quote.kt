package com.fndt.quote.domain.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    val id: Int,
    var body: String,
    @SerialName("created_at") val createdAt: Long,
    var author: Author,
    var likes: Int = 0,
)
