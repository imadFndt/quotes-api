package com.fndt.quote.domain.usecases.review

import com.fndt.quote.domain.UseCaseAdapter
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.base.SimpleRepository

class SimpleReviewUseCaseAdapter<T> internal constructor(
    private val itemId: Int,
    private val simpleRepository: SimpleRepository<T>,
    private val permissionsDelegate: (User?) -> Boolean,
    private val mapItem: (T) -> T,
) : UseCaseAdapter<T> {

    override fun hasPermissions(user: User?) = permissionsDelegate(user)

    override fun getItem(): T? = simpleRepository.findById(itemId)

    override fun addItem(item: T) {
        simpleRepository.add(mapItem(item))
    }

    override fun removeItem(item: T) {
        simpleRepository.remove(item)
    }

    companion object {
        fun createTagReviewAdapter(
            itemId: Int,
            repository: SimpleRepository<Tag>,
            permissionsManager: UserPermissionManager
        ): UseCaseAdapter<Tag> {
            return SimpleReviewUseCaseAdapter(
                itemId,
                repository,
                permissionsManager::hasAdminPermission,
            ) { it.copy(isPublic = true) }
        }

        fun createQuoteReviewAdapter(
            itemId: Int,
            repository: SimpleRepository<Quote>,
            permissionsManager: UserPermissionManager
        ): UseCaseAdapter<Quote> {
            return SimpleReviewUseCaseAdapter(
                itemId,
                repository,
                permissionsManager::hasModeratorPermission,
            ) { it.copy(isPublic = true) }
        }
    }
}
