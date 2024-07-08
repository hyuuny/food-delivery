package hyuuny.fooddelivery.likedstores.infrastructure

import hyuuny.fooddelivery.likedstores.domain.LikedStore
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface LikedStoreDao : CoroutineCrudRepository<LikedStore, Long> {

    suspend fun findAllByUserId(userId: Long): List<LikedStore>

    suspend fun findByUserIdAndStoreId(userId: Long, storeId: Long): LikedStore?

}
