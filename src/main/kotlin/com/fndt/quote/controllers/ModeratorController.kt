package com.fndt.quote.controllers

import com.fndt.quote.controllers.dto.QuoteReview
import com.fndt.quote.controllers.factory.ModeratorUseCaseFactory
import com.fndt.quote.controllers.util.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

class ModeratorController(private val useCaseFactory: ModeratorUseCaseFactory) : RoutingController {
    override fun route(routing: Routing) = routing.routePathWithAuth("") {
        addTag()
        banUser()
        reviewQuote()
    }

    private fun Route.addTag() {
        postExt(TAG_ENDPOINT) { principal ->
            val tagName = parameters[TAG_NAME_ARG] ?: run {
                respondText(text = BAD_JSON, status = HttpStatusCode.BadRequest)
                return@postExt
            }
            useCaseFactory.getAddTagUseCase(tagName, principal.user)
            respondText(SUCCESS)
        }
    }

    private fun Route.banUser() {
        postExt(BAN_ENDPOINT) { principal ->
            val quoteId = getAndCheckIntParameter(QUOTE_ID) ?: run {
                respondText(text = BAD_JSON, status = HttpStatusCode.BadRequest)
                return@postExt
            }
            useCaseFactory.getBanUseCase(quoteId, principal.user)
        }
    }

    private fun Route.reviewQuote() {
        postExt(REVIEW_QUOTE_ENDPOINT) { principal ->
            val (decision, quoteId) = receiveCatching<QuoteReview>() ?: run {
                respondText(text = BAD_JSON, status = HttpStatusCode.UnsupportedMediaType)
                return@postExt
            }
            useCaseFactory.getReviewQuoteUseCase(quoteId, decision, principal.user).run()
            respondText(SUCCESS)
        }
    }
}
