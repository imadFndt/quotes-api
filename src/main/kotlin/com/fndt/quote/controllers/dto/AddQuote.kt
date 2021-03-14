package com.fndt.quote.controllers.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddQuote(
    @SerialName("quote_id") val quoteId: Int? = null,
    val body: String,
    @SerialName("author_id") val authorId: Int,
    @SerialName("tags_id") val tagsId: List<Int> = emptyList(),
)
