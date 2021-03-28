package com.fndt.quote.data

import com.fndt.quote.data.util.toHashed
import com.fndt.quote.data.util.toUser
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.repository.UserRepository
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class UserRepositoryImpl(dbProvider: DatabaseProvider) : UserRepository {
    private val usersTable: DatabaseProvider.Users by dbProvider

    override fun getUsers(): List<User> {
        return usersTable.selectAll().map { it.toUser() }
    }

    override fun update(
        userId: Int,
        time: Long?,
        role: AuthRole?,
        password: String?,
        login: String?
    ): User? {
        return usersTable.update({ usersTable.id eq userId }) {
            role?.run { it[DatabaseProvider.Users.role] = role }
            password?.run { it[hashedPassword] = password.toHashed() }
            login?.run { it[name] = login }
            time?.run { it[blockedUntil] = time }
        }.let { findUserByParams(it) }
    }

    private fun update(user: User): ID {
        usersTable.update({ usersTable.id eq user.id }) {
            it[role] = user.role
            it[blockedUntil] = user.blockedUntil
        }
        return user.id
    }

    override fun add(user: User): ID {
        val userExists = findUserByParams(userId = user.id) != null
        return if (userExists) update(user) else insert(user)
    }

    override fun findUserByParams(userId: Int?, name: String?, password: String?, withPassword: Boolean): User? {
        return usersTable.selectAll()
            .apply { userId?.let { andWhere { usersTable.id eq userId } } }
            .apply { name?.let { andWhere { DatabaseProvider.Users.name eq name } } }
            .apply { password?.let { andWhere { DatabaseProvider.Users.hashedPassword eq password.toHashed() } } }
            .firstOrNull()?.toUser(withPassword)
    }

    private fun insert(user: User): ID {
        return usersTable.insert { insert ->
            insert[name] = user.name
            insert[hashedPassword] = user.hashedPassword.toHashed()
            insert[role] = AuthRole.REGULAR
        }[usersTable.id].value
    }
}
