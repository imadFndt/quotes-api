package com.fndt.quote.data

import com.fndt.quote.data.util.toHashed
import com.fndt.quote.data.util.toUser
import com.fndt.quote.domain.dao.UserDao
import com.fndt.quote.domain.dto.AuthRole
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

const val BLOCKED_FOREVER = -1L

class UserDaoImpl(dbProvider: DatabaseProvider) : UserDao {
    private val usersTable: DatabaseProvider.Users by dbProvider

    override fun update(
        userId: Int,
        time: Long?,
        role: AuthRole?,
        password: String?,
        login: String?
    ) = transaction {
        usersTable.update({ usersTable.id eq userId }) {
            role?.run {
                it[usersTable.role] = role
                if (role == AuthRole.BANNED) it[blockedUntil] = time ?: run { BLOCKED_FOREVER }
            }
            password?.run { it[usersTable.hashedPassword] = password.toHashed() }
            login?.run { it[usersTable.name] = login }
        }
        findUser(userId)
    }

    override fun insert(login: String, password: String) = transaction {
        findUser(
            usersTable.insert { insert ->
                insert[name] = login
                insert[hashedPassword] = password.toHashed()
                insert[role] = AuthRole.REGULAR
            }[usersTable.id].value
        )
    }

    override fun findUser(userId: Int?, name: String?, password: String?) = transaction {
        usersTable.selectAll()
            .apply { userId?.let { andWhere { usersTable.id eq userId } } }
            .apply { name?.let { andWhere { usersTable.name eq name } } }
            .apply { password?.let { andWhere { usersTable.hashedPassword eq password.toHashed() } } }
            .firstOrNull()?.toUser()
    }

    override fun getUsers() = transaction {
        usersTable.selectAll().map { it.toUser() }
    }
}
