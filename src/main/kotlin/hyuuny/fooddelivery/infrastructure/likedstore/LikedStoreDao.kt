package hyuuny.fooddelivery.infrastructure.likedstore

import hyuuny.fooddelivery.domain.likedstore.LikedStore
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface LikedStoreDao : CoroutineCrudRepository<LikedStore, Long> {

    suspend fun findAllByUserId(userId: Long): List<LikedStore>

    suspend fun findByUserIdAndStoreId(userId: Long, storeId: Long): LikedStore?

}
