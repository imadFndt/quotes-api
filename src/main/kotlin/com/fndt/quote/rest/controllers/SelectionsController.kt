package com.fndt.quote.rest.controllers

import com.fndt.quote.domain.filter.QuotesAccess
import com.fndt.quote.domain.filter.QuotesOrder
import com.fndt.quote.domain.usecases.quotes.*
import com.fndt.quote.rest.UrlSchemeProvider
import com.fndt.quote.rest.dto.out.toOutQuoteList
import com.fndt.quote.rest.factory.SelectionUseCaseFactory
import com.fndt.quote.rest.util.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect

class SelectionsController(private val useCaseFactory: SelectionUseCaseFactory) : RoutingController {
    override fun route(routing: Routing) = routing.routePathWithAuth(QUOTES_ENDPOINT) {
        getQuotes()
    }

    private fun Route.getQuotes() = getExt { principal ->
        processRequest {
            val args = initArgsMap()
            useCaseFactory.getSelectionsUseCase(args, principal.user).run()
        }.catch(defaultCatch()).collect { quotes ->
            respond(quotes.toOutQuoteList(UrlSchemeProvider))
        }
    }

    private fun ApplicationCall.initArgsMap(): Map<String, Any?> {
        val args = mutableMapOf<String, Any?>()
        args[QUERY_KEY] = parameters[QUERY_KEY] as? Any
        args[AUTHOR_KEY] = parameters[AUTHOR_KEY]?.toInt() as? Any
        args[USER_KEY] = parameters[USER_KEY]?.toInt() as? Any
        args[TAG_KEY] = parameters[TAG_KEY]?.toInt() as? Any
        args[PAGE_KEY] = parameters[PAGE_KEY]?.toInt() as? Any
        args[PER_PAGE_KEY] = parameters[PER_PAGE_KEY]?.toInt() as? Any

        val order = parameters[ORDER_KEY]?.let { QuotesOrder.findKey(it) ?: throw IllegalArgumentException(BAD_ORDER) }
        args[ORDER_KEY] = order as? Any

        val access =
            parameters[ACCESS_KEY]?.let { QuotesAccess.findKey(it) ?: throw IllegalArgumentException(BAD_ACCESS) }

        args[ACCESS_KEY] = access as? Any
        return args
    }
}
