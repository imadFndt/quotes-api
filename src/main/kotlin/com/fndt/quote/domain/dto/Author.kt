package com.fndt.quote.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class Author(
    val id: Int,
    var name: String,
)
