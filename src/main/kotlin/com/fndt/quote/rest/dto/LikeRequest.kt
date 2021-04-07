package com.fndt.quote.rest.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LikeRequest(
    @SerialName("quote_id") val quoteId: Int,
    @SerialName("like_action") val likeAction: Boolean,
)
