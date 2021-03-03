package com.fndt.quote

import com.fndt.quote.domain.QuotesService
import com.fndt.quote.domain.QuotesServiceImpl
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

val quotesService: QuotesService = QuotesServiceImpl()

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    routing {
        route("/test") {
            get { launch { quotesService.getQuotes().collectLatest { call.respond(it) } } }
        }
    }
}
