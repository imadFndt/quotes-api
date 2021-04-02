package com.fndt.quote.domain.dto

data class Quotes(
    val totalPages: Int,
    val page: Int,
    val quotes: List<Quote>
)
