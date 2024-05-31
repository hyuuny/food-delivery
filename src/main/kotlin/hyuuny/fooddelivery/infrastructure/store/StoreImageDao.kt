package hyuuny.fooddelivery.infrastructure.store

import hyuuny.fooddelivery.domain.store.StoreImage
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface StoreImageDao : CoroutineCrudRepository<StoreImage, Long> {
}