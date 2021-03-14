package com.fndt.quote.domain.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val UNDEFINED = 0

@Serializable
data class Quote(
    val id: Int = UNDEFINED,
    var body: String,
    @SerialName("created_at") val createdAt: Long,
    var author: Author,
    var likes: Int = 0,
    var tags: List<Tag> = emptyList(),
)
