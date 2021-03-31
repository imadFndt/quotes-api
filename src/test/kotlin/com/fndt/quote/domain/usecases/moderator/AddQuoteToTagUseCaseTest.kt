package com.fndt.quote.domain.usecases.moderator

import com.fndt.quote.domain.UseCaseTestInit
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.getDummyQuote
import com.fndt.quote.domain.getDummyUser
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.repository.TagSelectionRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AddQuoteToTagUseCaseTest : UseCaseTestInit() {

    @MockK(relaxed = true)
    lateinit var tagRepository: TagRepository

    @MockK(relaxed = true)
    lateinit var quoteRepository: QuoteRepository

    @MockK(relaxed = true)
    lateinit var tagSelectionRepository: TagSelectionRepository

    lateinit var useCase: AddQuoteToTagUseCase

    private val requestingUser: User = getDummyUser(AuthRole.MODERATOR)
    private val quoteId: Int = 1
    private val tagId: Int = 1

    @Test
    fun `add success`() = runBlocking {
        setConditions()
        useCase = AddQuoteToTagUseCase(
            quoteId,
            tagId,
            tagRepository,
            quoteRepository,
            tagSelectionRepository,
            requestingUser,
            permissionManager,
            requestManager
        )

        useCase.run()

        verify { tagSelectionRepository.add(any(), any()) }
    }

    @Test
    fun `add quote not found`() {
        useCase = setConditions(quote = null)

        assertThrows<IllegalStateException> { runBlocking { useCase.run() } }
    }

    @Test
    fun `add tag not found`() {
        useCase = setConditions(tag = null)

        assertThrows<IllegalStateException> { runBlocking { useCase.run() } }
    }

    private fun setConditions(
        quote: Quote? = getDummyQuote("", AuthRole.MODERATOR),
        tag: Tag? = Tag(name = "")
    ): AddQuoteToTagUseCase {
        every { quoteRepository.findById(any()) } returns quote
        every { tagRepository.findById(tagId) } returns tag
        return AddQuoteToTagUseCase(
            quoteId,
            tagId,
            tagRepository,
            quoteRepository,
            tagSelectionRepository,
            requestingUser,
            permissionManager,
            requestManager
        )
    }
}
