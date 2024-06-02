package hyuuny.fooddelivery.infrastructure.store

import hyuuny.fooddelivery.domain.store.StoreDetail

interface StoreDetailRepository {

    suspend fun insert(storeDetail: StoreDetail): StoreDetail

    suspend fun findByStoreId(storeId: Long): StoreDetail?

    suspend fun deleteByStoreId(storeId: Long)

}