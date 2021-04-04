package com.fndt.quote.rest.dto

import kotlinx.serialization.Serializable

@Serializable
data class AddQuote(
    val body: String,
    val authorName: String,
)
