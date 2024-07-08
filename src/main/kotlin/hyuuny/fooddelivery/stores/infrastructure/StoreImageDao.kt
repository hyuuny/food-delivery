package hyuuny.fooddelivery.stores.infrastructure

import hyuuny.fooddelivery.stores.domain.StoreImage
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface StoreImageDao : CoroutineCrudRepository<StoreImage, Long> {

    suspend fun findAllByStoreId(storeId: Long): List<StoreImage>

    suspend fun deleteAllByStoreId(storeId: Long)

}
