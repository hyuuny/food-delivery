package hyuuny.fooddelivery.application.store

import CreateStoreImageCommand
import CreateStoreImageRequest
import hyuuny.fooddelivery.domain.store.StoreImage
import hyuuny.fooddelivery.infrastructure.store.StoreImageRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class StoreImageUseCase(
    private val repository: StoreImageRepository
) {

    suspend fun createStoreImages(
        storeId: Long,
        request: CreateStoreImageRequest,
        now: LocalDateTime
    ): List<StoreImage> {
        val storeImages = request.imageUrls.map {
            StoreImage.handle(
                CreateStoreImageCommand(
                    storeId = storeId,
                    imageUrl = it,
                    createdAt = now
                )
            )
        }
        return repository.insertAll(storeImages)
    }

    suspend fun getStoreImagesByStoreId(storeId: Long): List<StoreImage> = repository.findAllByStoreId(storeId)

    suspend fun updateStoreImages(storeId: Long, request: CreateStoreImageRequest, now: LocalDateTime) {
        repository.deleteAllByStoreId(storeId)
        val storeImages = request.imageUrls.map {
            StoreImage.handle(
                CreateStoreImageCommand(
                    storeId = storeId,
                    imageUrl = it,
                    createdAt = now
                )
            )
        }
        repository.insertAll(storeImages)
    }

}