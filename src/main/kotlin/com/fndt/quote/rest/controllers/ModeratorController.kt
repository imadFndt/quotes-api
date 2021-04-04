package com.fndt.quote.rest.controllers

import com.fndt.quote.rest.dto.AddQuoteToTag
import com.fndt.quote.rest.dto.AddTag
import com.fndt.quote.rest.dto.QuoteReview
import com.fndt.quote.rest.factory.ModeratorUseCaseFactory
import com.fndt.quote.rest.util.*
import io.ktor.request.*
import io.ktor.routing.*
import kotlinx.coroutines.flow.catch

class ModeratorController(private val useCaseFactory: ModeratorUseCaseFactory) : RoutingController {
    override fun route(routing: Routing) = routing.routePathWithAuth("") {
        addTag()
        banUser()
        reviewQuote()
        addQuoteToTag()
    }

    private fun Route.addTag() {
        postExt(TAG_ENDPOINT) { principal ->
            processRequest {
                val (tagName) = receive<AddTag>()
                useCaseFactory.getAddTagUseCase(tagName, principal.user)
            }.catch(defaultCatch())
                .collectSuccessResponse(this)
        }
    }

    private fun Route.addQuoteToTag() {
        postExt("$TAG_ENDPOINT$ADD_ENDPOINT") { principal ->
            processRequest {
                val (quoteId, tagId) = receive<AddQuoteToTag>()
                useCaseFactory.getAddQuoteToTagUseCase(quoteId, tagId, principal.user).run()
            }.respondPostDefault(this)
        }
    }

    private fun Route.banUser() {
        postExt("$BAN_ENDPOINT/{$QUOTE_ID}") { principal ->
            processRequest {
                val quoteId = parameters[QUOTE_ID]!!.toInt()
                useCaseFactory.getBanUseCase(quoteId, principal.user).run()
            }.respondPostDefault(this)
        }
    }

    private fun Route.reviewQuote() {
        postExt(REVIEW_QUOTE_ENDPOINT) { principal ->
            processRequest {
                val (decision, quoteId) = receive<QuoteReview>()
                useCaseFactory.getReviewQuoteUseCase(quoteId, decision, principal.user).run()
            }.respondPostDefault(this)
        }
    }
}
