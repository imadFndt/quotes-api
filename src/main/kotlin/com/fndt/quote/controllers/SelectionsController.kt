package com.fndt.quote.controllers

import com.fndt.quote.controllers.factory.SelectionUseCaseFactory
import com.fndt.quote.controllers.util.*
import com.fndt.quote.domain.filter.QuotesAccess
import com.fndt.quote.domain.filter.QuotesOrder
import com.fndt.quote.domain.usecases.selections.*
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
        getQuotes()
    }

    private fun Route.getQuotes() {
        getExt("/testo") { principal ->
            val query = parameters[QUERY_KEY]

            val author = try {
                parameters[AUTHOR_KEY]?.toInt()
            } catch (e: NumberFormatException) {
                respondText(MISSING_PARAMETER, status = HttpStatusCode.NotAcceptable)
                return@getExt
            }
            val user = try {
                parameters[USER_KEY]?.toInt()
            } catch (e: NumberFormatException) {
                respondText(MISSING_PARAMETER, status = HttpStatusCode.NotAcceptable)
                return@getExt
            }
            val tag = try {
                parameters[TAG_KEY]?.toInt()
            } catch (e: NumberFormatException) {
                respondText(MISSING_PARAMETER, status = HttpStatusCode.NotAcceptable)
                return@getExt
            }

            val order = parameters[ORDER_KEY]?.let {
                QuotesOrder.findKey(it) ?: run {
                    respondText(MISSING_PARAMETER, status = HttpStatusCode.NotAcceptable)
                    return@getExt
                }
            }
            val access = parameters[ACCESS_KEY]?.let {
                QuotesAccess.findKey(it) ?: run {
                    respondText(MISSING_PARAMETER, status = HttpStatusCode.NotAcceptable)
                    return@getExt
                }
            }
            val args = mapOf(
                QUERY_KEY to query,
                AUTHOR_KEY to author,
                USER_KEY to user,
                TAG_KEY to tag,
                ORDER_KEY to order,
                ACCESS_KEY to access
            )
            respond(useCaseFactory.getSelectionsUseCase(args, principal.user).run())
        }
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
