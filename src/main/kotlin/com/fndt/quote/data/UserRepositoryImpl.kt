package com.fndt.quote.data

import com.fndt.quote.data.util.toHashed
import com.fndt.quote.data.util.toUser
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class UserRepositoryImpl(dbProvider: DatabaseProvider) : UserRepository {
    private val usersTable: DatabaseProvider.Users by dbProvider

    override fun update(
        userId: Int,
        time: Long?,
        role: AuthRole?,
        password: String?,
        login: String?
    ): User? {
        return usersTable.update({ usersTable.id eq userId }) {
            role?.run { it[usersTable.role] = role }
            password?.run { it[usersTable.hashedPassword] = password.toHashed() }
            login?.run { it[usersTable.name] = login }
            time?.run { it[usersTable.blockedUntil] = time }
        }.let { findUser(it) }
    }

    override fun insert(login: String, password: String): User? {
        return usersTable.insert { insert ->
            insert[name] = login
            insert[hashedPassword] = password.toHashed()
            insert[role] = AuthRole.REGULAR
        }[usersTable.id].value.let {
            findUser(it)
        }
    }

    override fun findUser(userId: Int?, name: String?, password: String?, withPassword: Boolean): User? {
        return usersTable.selectAll()
            .apply { userId?.let { andWhere { usersTable.id eq userId } } }
            .apply { name?.let { andWhere { usersTable.name eq name } } }
            .apply { password?.let { andWhere { usersTable.hashedPassword eq password.toHashed() } } }
            .firstOrNull()?.toUser(withPassword)
    }

    override fun getUsers(): List<User> {
        return usersTable.selectAll().map { it.toUser() }
    }
}
