package hyuuny.fooddelivery.stores.infrastructure

import hyuuny.fooddelivery.stores.domain.StoreImage
import kotlinx.coroutines.flow.toList
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component

@Component
class StoreImageRepositoryImpl(
    private val dao: StoreImageDao,
    private val template: R2dbcEntityTemplate,
) : StoreImageRepository {

    override suspend fun insertAll(storeImages: List<StoreImage>): List<StoreImage> = dao.saveAll(storeImages).toList()

    override suspend fun findAllByStoreId(storeId: Long): List<StoreImage> = dao.findAllByStoreId(storeId).toList()

    override suspend fun deleteAllByStoreId(storeId: Long) = dao.deleteAllByStoreId(storeId)
}
