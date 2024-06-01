package hyuuny.fooddelivery.application.store

import CreateStoreImageRequest
import hyuuny.fooddelivery.domain.store.StoreImage
import hyuuny.fooddelivery.infrastructure.store.StoreImageRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class CreateStoreImageUseCaseTest : BehaviorSpec({

    val repository = mockk<StoreImageRepository>()
    val useCase = StoreImageUseCase(repository)

    Given("매장 이미지를 등록하면서") {
        val request = CreateStoreImageRequest(
            imageUrls = listOf(
                "image-url-1.jpg",
                "image-url-2.jpg",
                "image-url-3.jpg",
            )
        )

        val now = LocalDateTime.now()
        val storeId = 1L
        val expectedStoreImages = listOf(
            StoreImage(
                id = 1,
                storeId = storeId,
                imageUrl = "image-url-1.jpg",
                createdAt = now
            ),
            StoreImage(
                id = 3,
                storeId = storeId,
                imageUrl = "image-url-2.jpg",
                createdAt = now
            ),
            StoreImage(
                id = 2,
                storeId = storeId,
                imageUrl = "image-url-3.jpg",
                createdAt = now
            ),
        )
        coEvery { repository.insertAll(any()) } returns expectedStoreImages

        `when`("요청한 이미지로") {
            val result = useCase.createStoreImages(storeId, request, now)

            then("매장 이미지를 등록할 수 있다.") {
                result.forEachIndexed { index, storeImage ->
                    storeImage.id.shouldNotBeNull()
                    storeImage.storeId shouldBe storeId
                    storeImage.imageUrl shouldBe request.imageUrls[index]
                    storeImage.createdAt shouldBe now
                }
            }
        }
    }

})