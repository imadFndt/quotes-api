package com.fndt.quote.controllers

import com.fndt.quote.controllers.factory.SelectionUseCaseFactory
import com.fndt.quote.controllers.util.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

class SelectionsController(private val useCaseFactory: SelectionUseCaseFactory) : RoutingController {
    override fun route(routing: Routing) = routing.routePathWithAuth("") {
        getPopulars()
        search()
        getTagSelection()
        getAuthorSelection()
    }

    private fun Route.getPopulars() {
        getExt(POPULARS_ENDPOINT) { principal -> respond(useCaseFactory.getPopularsUseCase(principal.user).run()) }
    }

    private fun Route.search() {
        getExt(SEARCH_ENDPOINT) { principal ->
            val query = parameters[QUERY_ARG] ?: run {
                respondText(QUERY_NOT_RECEIVED, status = HttpStatusCode.NotAcceptable)
                return@getExt
            }
            respond(useCaseFactory.getSearchUseCase(query, principal.user).run())
        }
    }

    private fun Route.getTagSelection() {
        getExt(TAG_ENDPOINT) { principal ->
            val tagId = getAndCheckIntParameter(ID) ?: return@getExt
            respond(useCaseFactory.getTagSelectionUseCase(tagId, principal.user).run())
        }
    }

    private fun Route.getAuthorSelection() {
        getExt(AUTHOR_ENDPOINT) { principal ->
            val authorId = getAndCheckIntParameter(ID) ?: return@getExt
            respond(useCaseFactory.getAuthorSelectionUseCase(authorId, principal.user).run())
        }
    }
}
