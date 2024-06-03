package hyuuny.fooddelivery.infrastructure.store

import StoreApiSearchCondition
import StoreSearchCondition
import hyuuny.fooddelivery.domain.store.Store
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

interface StoreRepository {

    suspend fun insert(store: Store): Store

    suspend fun findById(id: Long): Store?

    suspend fun update(store: Store)

    suspend fun delete(id: Long)

    suspend fun existsById(id: Long): Boolean

    suspend fun findAllStores(searchCondition: StoreSearchCondition, pageable: Pageable): PageImpl<Store>

    suspend fun findAllStores(searchCondition: StoreApiSearchCondition, pageable: Pageable): PageImpl<Store>

}