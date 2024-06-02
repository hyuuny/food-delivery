package hyuuny.fooddelivery.infrastructure.store

import hyuuny.fooddelivery.domain.store.Store
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component

@Component
class StoreRepositoryImpl(
    private val dao: StoreDao,
    private val template: R2dbcEntityTemplate,
) : StoreRepository {

    override suspend fun insert(store: Store): Store = dao.save(store)

    override suspend fun findById(id: Long): Store? = dao.findById(id)

    override suspend fun update(store: Store) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(store: Store) {
        TODO("Not yet implemented")
    }

    override suspend fun existsById(id: Long): Boolean {
        TODO("Not yet implemented")
    }
}