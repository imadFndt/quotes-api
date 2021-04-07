package com.fndt.quote.domain.usecases.add

import com.fndt.quote.domain.UseCaseInitTest
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.getDummyQuote
import com.fndt.quote.domain.getDummyUser
import com.fndt.quote.domain.manager.RepositoryProvider
import com.fndt.quote.domain.manager.getRepository
import com.fndt.quote.domain.repository.LikeRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.users.LikeUseCase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class LikeUseCaseTest : UseCaseInitTest() {
    private val like = Like(1, 1)

    @MockK(relaxed = true)
    lateinit var repositoryProvider: RepositoryProvider

    @MockK(relaxed = true)
    lateinit var quoteRepository: QuoteRepository

    @MockK(relaxed = true)
    lateinit var userRepository: UserRepository

    @MockK(relaxed = true)
    lateinit var likeRepository: LikeRepository

    private lateinit var useCase: LikeUseCase

    @BeforeEach
    override fun init() {
        super.init()
        every { repositoryProvider.getRepository<QuoteRepository>() } returns quoteRepository
        every { repositoryProvider.getRepository<UserRepository>() } returns userRepository
        every { repositoryProvider.getRepository<LikeRepository>() } returns likeRepository
    }

    @Test
    fun `regular like`() = runBlocking {
        useCase = getConditions(true, findLikeReturn = null)

        useCase.run()

        verify(exactly = 1) { likeRepository.add(any()) }
    }

    @Test
    fun `regular unlike`() = runBlocking {
        useCase = getConditions(false)

        useCase.run()

        verify(exactly = 1) { likeRepository.remove(any()) }
    }

    @Test
    fun `like already exists`() {
        useCase = getConditions(true)

        assertThrows<IllegalArgumentException> { runBlocking { useCase.run() } }
    }

    @Test
    fun `remove not existing like`() {
        useCase = getConditions(false, findLikeReturn = null)

        assertThrows<IllegalArgumentException> { runBlocking { useCase.run() } }
    }

    @Test
    fun `quote does not exist`() {
        useCase = getConditions(false, findQuoteReturn = null)

        assertThrows<IllegalArgumentException> { runBlocking { useCase.run() } }
    }

    @Test
    fun `user does not exist`() {
        useCase = getConditions(false, findUserReturn = null)

        assertThrows<IllegalArgumentException> { runBlocking { useCase.run() } }
    }

    private fun getConditions(
        likeAction: Boolean,
        findLikeReturn: Like? = like,
        findQuoteReturn: Quote? = getDummyQuote("a", AuthRole.REGULAR),
        findUserReturn: User? = getDummyUser(AuthRole.REGULAR)
    ): LikeUseCase {
        val requestingUser: User = getDummyUser(role = AuthRole.REGULAR)
        every { quoteRepository.findById(any()) } returns findQuoteReturn
        every { userRepository.findUserByParams(any()) } returns findUserReturn
        every { likeRepository.find(like) } returns findLikeReturn
        return LikeUseCase(
            like,
            likeAction,
            requestingUser,
            repositoryProvider,
            permissionManager,
            requestManager
        )
    }
}
