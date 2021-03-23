package com.fndt.quote.domain.usecases

import com.fndt.quote.domain.QuoteFilter
import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.getDummyUser
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.mockRunBlocking
import com.fndt.quote.domain.repository.TagRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TagSelectionUseCaseTest {

    @MockK(relaxed = true)
    lateinit var permissionManager: PermissionManager

    @MockK(relaxed = true)
    lateinit var filterBuilder: QuoteFilter.Builder

    @MockK(relaxed = true)
    lateinit var tagRepository: TagRepository

    @MockK(relaxed = true)
    lateinit var requestManager: RequestManager

    private lateinit var useCase: TagSelectionUseCase

    @BeforeEach
    fun init() {
        MockKAnnotations.init(this)
        requestManager.mockRunBlocking<User>()
        coEvery { permissionManager.hasTagSelectionsPermission(any()) } returns true
    }

    @Test
    fun success() = runBlocking {
        useCase = setUpConditions()

        useCase.run()

        verify { filterBuilder.setAccess(true) }
    }

    @Test
    fun `success moderator`() = runBlocking {
        useCase = setUpConditions(userRole = AuthRole.MODERATOR)

        useCase.run()

        verify { filterBuilder.setAccess(null) }
    }

    @Test
    fun `tag not found`() {
        useCase = setUpConditions(findTagReturn = null)

        assertThrows<IllegalStateException> { runBlocking { useCase.run() } }
    }

    @Test
    fun `user tries to look at private tag`() {
        useCase = setUpConditions(Tag(0, "a", false))

        assertThrows<IllegalStateException> { runBlocking { useCase.run() } }
    }

    private fun setUpConditions(
        findTagReturn: Tag? = Tag(0, "a", true),
        userRole: AuthRole = AuthRole.REGULAR
    ): TagSelectionUseCase {
        every { tagRepository.findById(any()) } returns findTagReturn
        return TagSelectionUseCase(
            1,
            filterBuilder,
            tagRepository,
            getDummyUser(userRole),
            permissionManager,
            requestManager
        )
    }
}
