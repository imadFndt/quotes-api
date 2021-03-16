package com.fndt.quote.data

import com.fndt.quote.data.util.toHashed
import com.fndt.quote.data.util.toUser
import com.fndt.quote.domain.dao.UserDao
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

const val BLOCKED_FOREVER = -1L

class UserDaoImpl(dbProvider: DatabaseProvider) : UserDao {
    private val usersTable: DatabaseProvider.Users by dbProvider

    override fun update(
        userId: Int,
        time: Long?,
        role: AuthRole?,
        password: String?,
        login: String?
    ): Int =
        transaction {
            usersTable.update({ usersTable.id eq userId }) {
                role?.run {
                    it[usersTable.role] = role
                    it[blockedUntil] = time ?: run { BLOCKED_FOREVER }
                }
                password?.run { it[usersTable.hashedPassword] = password.toHashed() }
                login?.run { it[usersTable.name] = login }
            }
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

    override fun findUser(userId: Int): User? = transaction {
        findUser { usersTable.id eq userId }
    }

    override fun findUser(userName: String): User? = transaction {
        findUser { usersTable.name eq userName }
    }

    override fun findUser(login: String, password: String): User? = transaction {
        findUser {
            (usersTable.name eq login) and
                (usersTable.hashedPassword eq password.toHashed())
        }
    }

    private fun findUser(where: SqlExpressionBuilder.() -> Op<Boolean>): User? {
        return usersTable.select(where).firstOrNull()?.toUser()
    }
}
