package com.fndt.quote.domain.manager

import com.fndt.quote.domain.dto.User

abstract class PermissionValidator {
    protected abstract fun validate(user: User?): Boolean
}
