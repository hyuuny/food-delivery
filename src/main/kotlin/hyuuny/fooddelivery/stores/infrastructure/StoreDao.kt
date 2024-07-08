package hyuuny.fooddelivery.stores.infrastructure

import hyuuny.fooddelivery.stores.domain.Store
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface StoreDao : CoroutineCrudRepository<Store, Long> {

    suspend fun findAllByNameContaining(name: String): List<Store>

    suspend fun findAllByIdIn(ids: List<Long>): List<Store>

    suspend fun findAllByNameLike(name: String): List<Store>

}
