package com.fndt.quote.domain

import com.fndt.quote.domain.manager.RequestManager
import com.fndt.quote.domain.manager.UserPermissionManager
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach

open class UseCaseInitTest {

    @MockK(relaxed = true)
    lateinit var permissionManager: UserPermissionManager

    @MockK(relaxed = true)
    lateinit var requestManager: RequestManager

    @BeforeEach
    open fun init() {
        MockKAnnotations.init(this)
        requestManager.mockRunBlocking<Unit>()
        coEvery { permissionManager.hasModeratorPermission(any()) } returns true
        coEvery { permissionManager.hasAdminPermission(any()) } returns true
        coEvery { permissionManager.isAuthorized(any()) } returns true
        coEvery { permissionManager.isRegisterAllowed() } returns true
        coEvery { permissionManager.isAuthAllowed() } returns true
    }
}
