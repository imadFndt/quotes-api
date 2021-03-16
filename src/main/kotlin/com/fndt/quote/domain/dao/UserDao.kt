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
    ): Int

    fun insert(login: String, password: String): User?
    fun findUser(userId: Int): User?
    fun findUser(userName: String): User?
    fun findUser(login: String, password: String): User?
}
