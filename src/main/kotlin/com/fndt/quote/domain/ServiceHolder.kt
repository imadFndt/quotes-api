package com.fndt.quote.domain

import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.services.AdminUserService
import com.fndt.quote.domain.services.ModeratorUserService
import com.fndt.quote.domain.services.RegularUserService

class ServiceHolder(factory: ServiceFactory) {
    val authService by lazy { factory.createAuthService() }

    val registrationService by lazy { factory.createRegistrationService() }

    private val adminUserService by lazy {
        factory.createUserService(AuthRole.ADMIN) as AdminUserService
    }

    private val moderatorUserService by lazy {
        factory.createUserService(AuthRole.MODERATOR) as ModeratorUserService
    }

    private val regularUserService by lazy {
        factory.createUserService(AuthRole.REGULAR) as RegularUserService
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : RegularUserService> getUserService(role: AuthRole?): T? {
        return when (role) {
            AuthRole.ADMIN -> if (roleMatches<AdminUserService>(role)) adminUserService as? T else null
            AuthRole.MODERATOR -> if (roleMatches<ModeratorUserService>(role)) moderatorUserService as? T else null
            AuthRole.REGULAR -> if (roleMatches<RegularUserService>(role)) regularUserService as? T else null
            AuthRole.NOT_AUTHORIZED -> null
            AuthRole.BANNED -> throw IllegalArgumentException("User is banned")
            null -> null
        }
    }

    private inline fun <reified T : RegularUserService> roleMatches(actualRole: AuthRole): Boolean {
        return when (T::class) {
            AdminUserService::class -> actualRole == AuthRole.ADMIN
            ModeratorUserService::class -> actualRole == AuthRole.ADMIN || actualRole == AuthRole.MODERATOR
            RegularUserService::class -> actualRole == AuthRole.ADMIN || actualRole == AuthRole.MODERATOR || actualRole == AuthRole.REGULAR
            else -> true
        }
    }
}