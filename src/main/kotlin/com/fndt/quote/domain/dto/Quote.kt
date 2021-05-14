package com.fndt.quote.domain.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val UNDEFINED = 0
typealias ID = Int

@Serializable
data class Quote(
    val id: ID = UNDEFINED,
    val body: String,
    val author: Author,
    @SerialName("created_at") val createdAt: Long,
    val user: User,
    val likes: Int = 0,
    val tags: List<Tag> = emptyList(),
    val isPublic: Boolean = false,
    @SerialName("did_i_like") val didILike: Boolean = false,
)
