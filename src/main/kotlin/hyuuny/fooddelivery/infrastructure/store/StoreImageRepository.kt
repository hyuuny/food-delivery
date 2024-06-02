package hyuuny.fooddelivery.infrastructure.store

import hyuuny.fooddelivery.domain.store.StoreImage

interface StoreImageRepository {

    suspend fun insertAll(storeImages: List<StoreImage>): List<StoreImage>

    suspend fun findAllByStoreId(storeId: Long): List<StoreImage>

}