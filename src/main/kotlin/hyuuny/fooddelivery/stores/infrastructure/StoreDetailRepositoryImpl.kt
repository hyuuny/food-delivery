package hyuuny.fooddelivery.stores.infrastructure

import hyuuny.fooddelivery.stores.domain.StoreDetail
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.insert
import org.springframework.data.r2dbc.core.usingAndAwait
import org.springframework.stereotype.Component

@Component
class StoreDetailRepositoryImpl(
    private val dao: StoreDetailDao,
    private val template: R2dbcEntityTemplate,
) : StoreDetailRepository {

    override suspend fun insert(storeDetail: StoreDetail): StoreDetail =
        template.insert<StoreDetail>().usingAndAwait(storeDetail)

    override suspend fun findByStoreId(storeId: Long): StoreDetail? = dao.findByStoreId(storeId)

    override suspend fun deleteByStoreId(storeId: Long) = dao.deleteByStoreId(storeId)

}
