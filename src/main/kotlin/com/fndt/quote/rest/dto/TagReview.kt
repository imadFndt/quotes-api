package com.fndt.quote.rest.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TagReview(val decision: Boolean, @SerialName("tag_id") val tagId: Int)
