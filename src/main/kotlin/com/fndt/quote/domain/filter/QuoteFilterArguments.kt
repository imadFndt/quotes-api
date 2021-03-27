package com.fndt.quote.domain.filter

import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User

data class QuoteFilterArguments(
    var tag: Tag? = null,
    var user: User? = null,
    var access: QuotesAccess = QuotesAccess.ALL,
    var order: QuotesOrder = QuotesOrder.LATEST,
    var query: String? = null,
    var quoteId: Int? = null,
    var authorId: Int? = null,
)
