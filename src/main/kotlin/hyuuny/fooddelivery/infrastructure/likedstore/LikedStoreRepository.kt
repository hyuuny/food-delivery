package hyuuny.fooddelivery.infrastructure.likedstore

import hyuuny.fooddelivery.domain.likedstore.LikedStore

interface LikedStoreRepository {

    suspend fun insert(likedStore: LikedStore): LikedStore

    suspend fun findById(id: Long): LikedStore?

    suspend fun findByUserIdAndStoreId(userId: Long, storeId: Long): LikedStore?

    suspend fun findAllByUserId(userId: Long): List<LikedStore>

    suspend fun delete(id: Long)

}
