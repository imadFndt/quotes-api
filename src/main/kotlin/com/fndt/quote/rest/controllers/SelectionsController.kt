package com.fndt.quote.rest.controllers

import com.fndt.quote.domain.filter.QuotesAccess
import com.fndt.quote.domain.filter.QuotesOrder
import com.fndt.quote.domain.manager.UrlSchemeProvider
import com.fndt.quote.domain.usecases.*
import com.fndt.quote.rest.dto.out.toOutQuoteList
import com.fndt.quote.rest.factory.SelectionUseCaseFactory
import com.fndt.quote.rest.util.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

class SelectionsController(private val useCaseFactory: SelectionUseCaseFactory) : RoutingController {
    override fun route(routing: Routing) = routing.routePathWithAuth(QUOTES_ENDPOINT) {
        getQuotes()
    }

    private fun Route.getQuotes() = getExt { principal ->
        val args = mutableMapOf<String, Any?>()
        args[QUERY_KEY] = parameters[QUERY_KEY] as? Any

        try {
            args[AUTHOR_KEY] = parameters[AUTHOR_KEY]?.toInt() as? Any
            args[USER_KEY] = parameters[USER_KEY]?.toInt() as? Any
            args[TAG_KEY] = parameters[TAG_KEY]?.toInt() as? Any
            args[PAGE_KEY] = parameters[PAGE_KEY]?.toInt() as? Any
            args[PER_PAGE_KEY] = parameters[PER_PAGE_KEY]?.toInt() as? Any
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
        args[ORDER_KEY] = order as? Any

        val access = parameters[ACCESS_KEY]?.let {
            QuotesAccess.findKey(it) ?: run {
                respondText(MISSING_PARAMETER, status = HttpStatusCode.NotAcceptable)
                return@getExt
            }
        }
        args[ACCESS_KEY] = access as? Any

        useCaseFactory.getSelectionsUseCase(args, principal.user).run()
            .also { respond(it.toOutQuoteList(UrlSchemeProvider)) }
    }
}
