package hyuuny.fooddelivery.categories.application

import UpdateCategoryRequest
import hyuuny.fooddelivery.categories.domain.Category
import hyuuny.fooddelivery.categories.infrastructure.CategoryRepository
import hyuuny.fooddelivery.common.constant.DeliveryType
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class UpdateCategoryUseCaseTest : BehaviorSpec({

    val repository = mockk<CategoryRepository>()
    val useCase = CategoryUseCase(repository)

    Given("카테고리를") {
        val categoryId = 1L
        val now = LocalDateTime.now()
        val category = Category(
            id = categoryId,
            deliveryType = DeliveryType.OUTSOURCING,
            name = "족발/보쌈",
            priority = 1,
            iconImageUrl = "pig-foot-image-url",
            visible = true,
            createdAt = now,
            updatedAt = now
        )

        val request = UpdateCategoryRequest(
            deliveryType = DeliveryType.OUTSOURCING,
            name = "피자",
            iconImageUrl = "icon-image-url.jpg",
            visible = false,
        )
        coEvery { repository.findById(any()) } returns category
        coEvery { repository.update(any()) } returns Unit

        `when`("입력한 정보로") {
            useCase.updateCategory(categoryId, request)

            then("수정할 수 있다.") {
                coEvery { repository.update(any()) }
            }
        }
    }

})
