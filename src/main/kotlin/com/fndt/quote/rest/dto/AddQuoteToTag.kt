package com.fndt.quote.rest.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddQuoteToTag(@SerialName("quote_id") val quoteId: Int, @SerialName("tag_id") val tagId: Int)
