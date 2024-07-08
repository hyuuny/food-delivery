package hyuuny.fooddelivery.stores.infrastructure

import hyuuny.fooddelivery.stores.domain.StoreDetail

interface StoreDetailRepository {

    suspend fun insert(storeDetail: StoreDetail): StoreDetail

    suspend fun findByStoreId(storeId: Long): StoreDetail?

    suspend fun deleteByStoreId(storeId: Long)

}
