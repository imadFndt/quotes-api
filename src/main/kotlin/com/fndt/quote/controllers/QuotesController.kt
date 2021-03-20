package com.fndt.quote.controllers

import com.fndt.quote.controllers.dto.AddComment
import com.fndt.quote.controllers.dto.AddQuote
import com.fndt.quote.controllers.dto.LikeRequest
import com.fndt.quote.controllers.util.*
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.manager.CommentsUseCaseManager
import com.fndt.quote.domain.manager.QuotesUseCaseManager
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*

const val QUOTES_ENDPOINT = "/quotes"
const val LIKE_ENDPOINT = "/like"
const val ID = "id"
const val COMMENTS_ENDPOINT = "{$ID}/comment"
const val DELETE_COMMENT_ENDPOINT = "/comment/{$ID}"

class QuotesController(
    private val quotesUseCaseManager: QuotesUseCaseManager,
    private val commentsUseCaseManager: CommentsUseCaseManager,
) : RoutingController {

    override fun route(routing: Routing) = routing.routePathWithAuth(QUOTES_ENDPOINT) {
        getExt { principal ->
            respond(quotesUseCaseManager.getQuotesUseCase(userRequesting = principal.user).run())
        }
        getExt { principal ->
            quotesUseCaseManager
                .getQuotesUseCase(userRequesting = principal.user).run()
                .also { respond(it) }
        }
        postExt { principal ->
            val quote = receiveCatching<AddQuote>()
            quote ?: return@postExt
            try {
                quotesUseCaseManager
                    .addQuotesUseCase(quote.body, quote.authorId).run()
                respondText("Success")
            } catch (e: IllegalStateException) {
                respondText("Failure", status = HttpStatusCode.BadRequest)
            }
        }
        postExt(LIKE_ENDPOINT) { principal ->
            val (quoteId, action) = receiveCatching<LikeRequest>() ?: return@postExt
            val userId = principal.user.id
            try {
                quotesUseCaseManager.likeQuoteUseCase(Like(quoteId, userId), action).run()
                respondText("Like success")
            } catch (e: IllegalStateException) {
                respondText("Like failed", status = HttpStatusCode.Conflict)
            }
        }
        getExt(COMMENTS_ENDPOINT) { principal ->
            getAndCheckIntParameter(ID)?.let { respond(commentsUseCaseManager.getCommentsUseCase(it).run()) }
        }
        postExt(COMMENTS_ENDPOINT) { principal ->
            receiveText()
            val (body, quoteId) = receiveCatching<AddComment>() ?: return@postExt

            respond(
                commentsUseCaseManager.addCommentsUseCase(body, quoteId, principal.user).run()
            )
        }
    }
}
