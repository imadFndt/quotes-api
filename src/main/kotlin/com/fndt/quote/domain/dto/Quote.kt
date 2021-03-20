package com.fndt.quote.domain.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val UNDEFINED = 0
const val NOT_YET_CREATED = 0L

@Serializable
data class Quote(
    val id: Int = UNDEFINED,
    var body: String,
    @SerialName("created_at") val createdAt: Long = NOT_YET_CREATED,
    var user: User,
    var likes: Int = 0,
    var tags: List<Tag> = emptyList(),
    val isPublic: Boolean = false
)
