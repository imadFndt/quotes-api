package com.fndt.quote.rest.controllers

import com.fndt.quote.rest.dto.AddQuoteToTag
import com.fndt.quote.rest.dto.AddTag
import com.fndt.quote.rest.dto.TagReview
import com.fndt.quote.rest.factory.TagsUseCaseFactory
import com.fndt.quote.rest.util.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect

class TagsController(private val useCaseFactory: TagsUseCaseFactory) : RoutingController {
    override fun route(routing: Routing) = routing.routePathWithAuth(TAG_ENDPOINT) {
        getTags()
        addTag()
        addQuoteToTag()
        reviewTag()
    }

    private fun Route.getTags() = getExt { principal ->
        processRequest {
            val access = parameters.getAccess()
            useCaseFactory.getTagsUseCase(access, principal.user).run()
        }.catch(defaultCatch()).collect {
            respond(it)
        }
    }

    private fun Route.addTag() = postExt { principal ->
        processRequest {
            val (tagName) = receive<AddTag>()
            useCaseFactory.getAddTagUseCase(tagName, principal.user).run()
        }.catch(defaultCatch())
            .collectSuccessResponse(this)
    }

    private fun Route.addQuoteToTag() = postExt(ADD_ENDPOINT) { principal ->
        processRequest {
            val (quoteId, tagId) = receive<AddQuoteToTag>()
            useCaseFactory.getAddQuoteToTagUseCase(quoteId, tagId, principal.user).run()
        }.respondPostDefault(this)
    }

    private fun Route.reviewTag() = postExt(REVIEW_ENDPOINT) { principal ->
        processRequest {
            val (decision, id) = receive<TagReview>()
            useCaseFactory.getApproveTagUseCase(id, decision, principal.user).run()
        }.respondPostDefault(this)
    }
}
