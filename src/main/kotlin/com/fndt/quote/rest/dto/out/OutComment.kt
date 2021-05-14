package com.fndt.quote.rest.dto.out

import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.dto.UNDEFINED
import com.fndt.quote.rest.UrlSchemeProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class OutComment(
    val id: ID = UNDEFINED,
    val body: String,
    @SerialName("quote_id") val quoteId: Int,
    @SerialName("created_at") val createdAt: Long,
    val user: OutUser
)

fun Comment.toOutComment(schemeProvider: UrlSchemeProvider) =
    OutComment(id, body, quoteId, createdAt, user.toOutUser(schemeProvider))
