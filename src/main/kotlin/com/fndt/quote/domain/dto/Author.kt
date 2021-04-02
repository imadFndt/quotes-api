package com.fndt.quote.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class Author(
    val id: ID = 0,
    val name: String
)
