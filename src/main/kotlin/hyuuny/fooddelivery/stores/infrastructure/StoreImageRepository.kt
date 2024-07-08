package hyuuny.fooddelivery.stores.infrastructure

import hyuuny.fooddelivery.stores.domain.StoreImage

interface StoreImageRepository {

    suspend fun insertAll(storeImages: List<StoreImage>): List<StoreImage>

    suspend fun findAllByStoreId(storeId: Long): List<StoreImage>

    suspend fun deleteAllByStoreId(storeId: Long)

}
