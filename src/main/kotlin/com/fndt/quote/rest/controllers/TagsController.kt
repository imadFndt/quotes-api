package com.fndt.quote.rest.controllers

import com.fndt.quote.rest.dto.AddQuoteToTag
import com.fndt.quote.rest.dto.AddTag
import com.fndt.quote.rest.dto.TagReview
import com.fndt.quote.rest.factory.TagsUseCaseFactory
import com.fndt.quote.rest.util.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

class TagsController(private val useCaseFactory: TagsUseCaseFactory) : RoutingController {
    override fun route(routing: Routing) = routing.routePathWithAuth(TAG_ENDPOINT) {
        getTags()
        addTag()
        addQuoteToTag()
        reviewTag()
    }

    private fun Route.getTags() = getExt { principal ->
        val access = parameters.getAccess()
        val tags = useCaseFactory.getTagsUseCase(access, principal.user).run()
        respond(tags)
    }

    private fun Route.addTag() = postExt { principal ->
        val (tagName) = receive<AddTag>()
        useCaseFactory.getAddTagUseCase(tagName, principal.user).run()
        respond(SUCCESS)
    }

    private fun Route.addQuoteToTag() = postExt(ADD_ENDPOINT) { principal ->
        val (quoteId, tagId) = receive<AddQuoteToTag>()
        useCaseFactory.getAddQuoteToTagUseCase(quoteId, tagId, principal.user).run()
        respond(SUCCESS)
    }

    private fun Route.reviewTag() = postExt(REVIEW_ENDPOINT) { principal ->
        val (decision, id) = receive<TagReview>()
        useCaseFactory.getApproveTagUseCase(id, decision, principal.user).run()
        respond(SUCCESS)
    }
}
