package com.fndt.quote

import com.fndt.quote.data.*
import com.fndt.quote.domain.QuotesBrowseService
import com.fndt.quote.domain.QuotesEditService
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val QUOTES_ENDPOINT = "/quotes"
const val AUTHORS_ENDPOINT = "/authors"
val browseService: QuotesBrowseService = QuotesBrowseServiceImpl(DbProvider.Quotes, DbProvider.Authors)
val editService: QuotesEditService = QuotesEditServiceImpl(DbProvider.Quotes, DbProvider.Authors)

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) { gson { setPrettyPrinting() } }
    install(Authentication) { configureAuth() }
    routing { setUpRouting() }
}

fun Routing.setUpRouting() {
    DbProvider.initDb
    route(QUOTES_ENDPOINT) {
        get {
            call.respond(browseService.getQuotes())
        }
        post {
            call.receiveOrNull<Quote>()?.let { editService.upsertQuote(it) }
        }
        post("/like") {
            call.receiveOrNull<Like>()?.let {
                browseService.setQuoteLike(it)
                call.respondText("Like put successfully")
            }
        }
    }
    route(AUTHORS_ENDPOINT) {
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            try {
                id.toInt()
                call.respond(browseService.getQuotesByAuthorId(id.toInt()))
            } catch (e: NumberFormatException) {
                call.respondText("Malformed id", status = HttpStatusCode.BadRequest)
            }
        }
    }
    authenticate {
        get("/test") {
            call.respondText("Success, ${call.principal<UserIdPrincipal>()?.name}")
        }
    }
}

fun Authentication.Configuration.configureAuth() {
    basic {
        realm = "Ktor"
        validate { credentials ->
            if (credentials.name == credentials.password) UserIdPrincipal(credentials.name) else null
        }
    }
}

/*
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"body":"Тело цитаты","date":"30", "author": {"id":3,"name":"Копатыч"}}' \
  http://0.0.0.0:8080/quotes
 */
