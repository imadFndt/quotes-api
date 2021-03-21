package com.fndt.quote.domain.repository

import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.dto.User

interface UserRepository {
    fun getUsers(): List<User>
    fun add(user: User): ID

    fun update(
        userId: Int,
        time: Long? = null,
        role: AuthRole? = null,
        password: String? = null,
        login: String? = null
    ): User?

    fun findUserByParams(
        userId: Int? = null,
        name: String? = null,
        password: String? = null,
        withPassword: Boolean = false
    ): User?
}
