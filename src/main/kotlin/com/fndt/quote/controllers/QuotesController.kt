package com.fndt.quote.controllers

import com.fndt.quote.controllers.dto.AddQuote
import com.fndt.quote.controllers.dto.LikeRequest
import com.fndt.quote.controllers.factory.QuotesUseCaseFactory
import com.fndt.quote.controllers.util.*
import com.fndt.quote.domain.dto.Like
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*

class QuotesController(private val useCaseFactory: QuotesUseCaseFactory) : RoutingController {

    override fun route(routing: Routing) = routing.routePathWithAuth(QUOTES_ENDPOINT) {
        getExt { principal -> respond(useCaseFactory.getQuotesUseCase(principal.user).run()) }
        postExt { principal ->
            val quote = receiveCatching<AddQuote>() ?: run {
                respondText("Wrong parameters")
                return@postExt
            }
            try {
                useCaseFactory.addQuotesUseCase(quote.body, principal.user).run()
                respondText("Success")
            } catch (e: IllegalStateException) {
                respondText(e.message.toString(), status = HttpStatusCode.BadRequest)
            }
        }
        postExt(LIKE_ENDPOINT) { principal ->
            val (quoteId, action) = receiveCatching<LikeRequest>() ?: return@postExt
            try {
                useCaseFactory.likeQuoteUseCase(Like(quoteId, principal.user.id), action, principal.user).run()
                respondText("Like success")
            } catch (e: IllegalStateException) {
                respondText("Like failed", status = HttpStatusCode.Conflict)
            }
        }
    }
}
