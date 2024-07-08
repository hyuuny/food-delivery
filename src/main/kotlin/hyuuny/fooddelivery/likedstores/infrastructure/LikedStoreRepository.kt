package hyuuny.fooddelivery.likedstores.infrastructure

import hyuuny.fooddelivery.likedstores.domain.LikedStore

interface LikedStoreRepository {

    suspend fun insert(likedStore: LikedStore): LikedStore

    suspend fun findById(id: Long): LikedStore?

    suspend fun findByUserIdAndStoreId(userId: Long, storeId: Long): LikedStore?

    suspend fun findAllByUserId(userId: Long): List<LikedStore>

    suspend fun delete(id: Long)

}
