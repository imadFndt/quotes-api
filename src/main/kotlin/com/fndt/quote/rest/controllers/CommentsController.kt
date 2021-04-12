package com.fndt.quote.rest.controllers

import com.fndt.quote.rest.UrlSchemeProvider
import com.fndt.quote.rest.dto.AddComment
import com.fndt.quote.rest.dto.out.toOutComment
import com.fndt.quote.rest.factory.CommentsUseCaseFactory
import com.fndt.quote.rest.util.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

class CommentsController(private val useCaseFactory: CommentsUseCaseFactory) : RoutingController {
    override fun route(routing: Routing) = routing.routePathWithAuth(COMMENTS_ENDPOINT) {
        getComments()
        addComment()
    }

    private fun Route.getComments() = getExt { principal ->
        val id = parameters[ID]!!.toInt()
        val comments = useCaseFactory.getCommentsUseCase(id, principal.user).run().map {
            it.toOutComment(UrlSchemeProvider)
        }
        respond(comments)
    }

    private fun Route.addComment() = postExt { principal ->
        val quoteId = parameters[ID]!!.toInt()
        val (body) = receive<AddComment>()
        useCaseFactory.addCommentsUseCase(body, quoteId, principal.user).run()
        respond(SUCCESS)
    }
}
