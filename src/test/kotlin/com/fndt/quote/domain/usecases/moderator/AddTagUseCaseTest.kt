package com.fndt.quote.domain.usecases.moderator

import com.fndt.quote.domain.UseCaseTestInit
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.getDummyUser
import com.fndt.quote.domain.repository.TagRepository
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class AddTagUseCaseTest : UseCaseTestInit() {

    @MockK(relaxed = true)
    lateinit var tagRepository: TagRepository

    lateinit var useCase: AddTagUseCase

    private val requestingUser: User = getDummyUser(AuthRole.MODERATOR)
    private val tagName: String = "tag"

    @Test
    fun `add success`() = runBlocking {
        useCase = AddTagUseCase(tagName, tagRepository, requestingUser, permissionManager, requestManager)
        useCase.run()
        verify { tagRepository.add(any()) }
    }
}
