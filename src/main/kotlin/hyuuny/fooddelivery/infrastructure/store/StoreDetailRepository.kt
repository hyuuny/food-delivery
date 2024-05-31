package hyuuny.fooddelivery.infrastructure.store

import hyuuny.fooddelivery.domain.store.StoreDetail

interface StoreDetailRepository {

    suspend fun insert(storeDetail: StoreDetail): StoreDetail

}