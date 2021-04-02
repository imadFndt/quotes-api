package com.fndt.quote.domain.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val UNDEFINED = 0
typealias ID = Int

@Serializable
data class Quote(
    val id: ID = UNDEFINED,
    var body: String,
    val author: Author,
    @SerialName("created_at") val createdAt: Long,
    var user: User,
    var likes: Int = 0,
    var tags: List<Tag> = emptyList(),
    val isPublic: Boolean = false
)
