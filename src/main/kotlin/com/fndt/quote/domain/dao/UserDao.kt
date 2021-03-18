package com.fndt.quote.domain.dao

import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User

interface UserDao {
    fun update(
        userId: Int,
        time: Long? = null,
        role: AuthRole? = null,
        password: String? = null,
        login: String? = null
    ): User?

    fun insert(login: String, password: String): User?
    fun findUser(
        userId: Int? = null,
        name: String? = null,
        password: String? = null,
        withPassword: Boolean = false
    ): User?

    fun getUsers(): List<User>
}
