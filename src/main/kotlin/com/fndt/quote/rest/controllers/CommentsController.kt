package com.fndt.quote.rest.controllers

import com.fndt.quote.rest.util.defaultCatch
import com.fndt.quote.rest.dto.AddComment
import com.fndt.quote.rest.factory.CommentsUseCaseFactory
import com.fndt.quote.rest.util.processRequest
import com.fndt.quote.rest.util.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect

class CommentsController(private val useCaseFactory: CommentsUseCaseFactory) : RoutingController {
    override fun route(routing: Routing) = routing.routePathWithAuth(COMMENTS_ENDPOINT) {
        getComments()
        addComment()
    }

    private fun Route.getComments() {
        getExt { principal ->
            processRequest {
                parameters[ID]!!.toInt()
            }.catch(defaultCatch()).collect {
                respond(useCaseFactory.getCommentsUseCase(it, principal.user).run())
            }
        }
    }

    private fun Route.addComment() {
        postExt { principal ->
            processRequest {
                val quoteId = parameters[ID]!!.toInt()
                val (body) = receive<AddComment>()
                useCaseFactory.addCommentsUseCase(body, quoteId, principal.user).run()
            }.catch(defaultCatch()).collect {
                respond(SUCCESS)
            }
        }
    }
}
