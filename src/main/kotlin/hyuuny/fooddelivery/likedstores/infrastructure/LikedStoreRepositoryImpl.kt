package hyuuny.fooddelivery.likedstores.infrastructure

import hyuuny.fooddelivery.likedstores.domain.LikedStore
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component

@Component
class LikedStoreRepositoryImpl(
    private val dao: LikedStoreDao,
    private val template: R2dbcEntityTemplate,
) : LikedStoreRepository {

    override suspend fun insert(likedStore: LikedStore): LikedStore = dao.save(likedStore)

    override suspend fun findById(id: Long): LikedStore? = dao.findById(id)

    override suspend fun findByUserIdAndStoreId(userId: Long, storeId: Long): LikedStore? =
        dao.findByUserIdAndStoreId(userId, storeId)

    override suspend fun findAllByUserId(userId: Long): List<LikedStore> = dao.findAllByUserId(userId)

    override suspend fun delete(id: Long) = dao.deleteById(id)

}
