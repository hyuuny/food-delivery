package hyuuny.fooddelivery.application.store

import hyuuny.fooddelivery.domain.store.StoreImage
import hyuuny.fooddelivery.infrastructure.store.StoreImageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class StoreImageUseCase(
    private val repository: StoreImageRepository
) {

    suspend fun getStoreImagesByStoreId(storeId: Long): List<StoreImage> = repository.findAllByStoreId(storeId)

    @Transactional
    suspend fun deleteStoreImagesByStoreId(storeId: Long) = repository.deleteAllByStoreId(storeId)

}