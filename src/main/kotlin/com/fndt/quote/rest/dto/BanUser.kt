package com.fndt.quote.rest.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BanUser(
    @SerialName("user_id")val userId: Int,
    @SerialName("ban_time") val banTime: Int
)
