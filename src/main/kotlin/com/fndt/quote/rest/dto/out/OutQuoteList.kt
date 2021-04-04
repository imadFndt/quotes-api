package com.fndt.quote.rest.dto.out

import com.fndt.quote.domain.dto.Quotes
import com.fndt.quote.domain.manager.UrlSchemeProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OutQuoteList(
    val page: Int,
    @SerialName("total_pages") val totalPages: Int,
    val quotes: List<OutQuote>,
)

fun Quotes.toOutQuoteList(urlProvider: UrlSchemeProvider): OutQuoteList {
    return OutQuoteList(page, totalPages, quotes.map { it.toOutQuote(urlProvider) })
}
