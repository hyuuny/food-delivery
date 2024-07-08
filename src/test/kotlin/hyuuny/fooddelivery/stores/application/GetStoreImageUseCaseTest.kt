package hyuuny.fooddelivery.stores.application

import hyuuny.fooddelivery.stores.domain.StoreImage
import hyuuny.fooddelivery.stores.infrastructure.StoreImageRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class GetStoreImageUseCaseTest : BehaviorSpec({

    val repository = mockk<StoreImageRepository>()
    val useCase = StoreImageUseCase(repository)

    Given("매장 이미지를 상세조회 할 때") {
        val storeId = 1L
        val now = LocalDateTime.now()
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
        coEvery { repository.findAllByStoreId(any()) } returns expectedStoreImages

        `when`("존재하는 매장 아이디이면") {
            val result = useCase.getStoreImagesByStoreId(storeId)

            then("매장 이미지를 상세조회 할 수 있다.") {
                result.forEachIndexed { index, storeImage ->
                    storeImage.id.shouldNotBeNull()
                    storeImage.storeId shouldBe storeId
                    storeImage.imageUrl shouldBe expectedStoreImages[index].imageUrl
                    storeImage.createdAt shouldBe now
                }
            }
        }

        `when`("존재하지 않는 매장 아이디이면") {
            coEvery { repository.findAllByStoreId(any()) } returns emptyList()
            val result = useCase.getStoreImagesByStoreId(storeId)

            then("비어있는 사진 목록이 반환된다.") {
                result.size shouldBe 0
            }
        }
    }

})
