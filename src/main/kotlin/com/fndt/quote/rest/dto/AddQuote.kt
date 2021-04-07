package com.fndt.quote.rest.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddQuote(
    val body: String,
    @SerialName("author_name") val authorName: String,
)
