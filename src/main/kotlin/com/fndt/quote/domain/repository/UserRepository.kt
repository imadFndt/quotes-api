package com.fndt.quote.domain.repository

import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.repository.base.BaseRepository

interface UserRepository : BaseRepository {
    fun getUsers(): List<User>
    fun add(user: User): ID
    fun remove(user: User)

    fun findUserByParams(
        userId: Int? = null,
        name: String? = null,
        password: String? = null,
        withPassword: Boolean = false
    ): User?
}
