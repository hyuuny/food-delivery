package hyuuny.fooddelivery.stores.application

import hyuuny.fooddelivery.stores.domain.StoreImage
import hyuuny.fooddelivery.stores.infrastructure.StoreImageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class StoreImageUseCase(
    private val repository: StoreImageRepository
) {

    suspend fun getStoreImagesByStoreId(storeId: Long): List<StoreImage> = repository.findAllByStoreId(storeId)

}
