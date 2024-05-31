package hyuuny.fooddelivery.infrastructure.store

import hyuuny.fooddelivery.domain.store.StoreDetail
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
}