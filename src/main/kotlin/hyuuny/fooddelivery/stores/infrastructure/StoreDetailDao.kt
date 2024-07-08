package hyuuny.fooddelivery.stores.infrastructure

import hyuuny.fooddelivery.stores.domain.StoreDetail
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface StoreDetailDao : CoroutineCrudRepository<StoreDetail, Long> {

    suspend fun findByStoreId(storeId: Long): StoreDetail

    suspend fun deleteByStoreId(storeId: Long)

}
