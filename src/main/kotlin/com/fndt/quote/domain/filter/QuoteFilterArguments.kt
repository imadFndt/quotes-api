package com.fndt.quote.domain.filter

import com.fndt.quote.domain.dto.User

data class QuoteFilterArguments(
    var tagId: Int? = null,
    var user: User? = null,
    var quoteAccess: Access = Access.ALL,
    var order: QuotesOrder = QuotesOrder.LATEST,
    var query: String? = null,
    var quoteId: Int? = null,
    var authorId: Int? = null,
    var tagAccess: Access = Access.PUBLIC,
    var requestingUser: User? = null
)
