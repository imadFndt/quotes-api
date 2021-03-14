package com.fndt.quote.data

import com.fndt.quote.data.util.transactionWithIO
import com.fndt.quote.domain.AdminUserService
import com.fndt.quote.domain.dto.AuthRole
import org.jetbrains.exposed.sql.update

class AdminUserServiceImpl(provider: DatabaseDefinition) : ModeratorUserServiceImpl(provider), AdminUserService {
    override suspend fun setTagVisibility(tagId: Int, isPublic: Boolean): Boolean = transactionWithIO {
        tagsTable.update({ tagsTable.id eq tagId }) { it[this.isPublic] = isPublic } != 0
    }

    override suspend fun changeRole(userId: Int, newRole: AuthRole, oldRole: AuthRole): Boolean = transactionWithIO {
        usersTable.update({ usersTable.id eq userId }) { it[this.role] = newRole } != 0
    }
}
