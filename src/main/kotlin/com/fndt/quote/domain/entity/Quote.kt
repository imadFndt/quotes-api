package com.fndt.quote.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    val body: String,
    val date: Long
)