package com.fndt.quote.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Author(
    val name: String,
    val id: String,
    val quotes: List<Quote>
)