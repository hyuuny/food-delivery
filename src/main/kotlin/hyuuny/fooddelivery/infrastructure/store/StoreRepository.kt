package hyuuny.fooddelivery.infrastructure.store

import hyuuny.fooddelivery.domain.store.Store

interface StoreRepository {

    suspend fun insert(store: Store): Store

    suspend fun findById(id: String): Store?

    suspend fun update(store: Store)

    suspend fun delete(store: Store)

    suspend fun existsById(id: String): Boolean

}