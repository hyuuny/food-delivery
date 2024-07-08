package hyuuny.fooddelivery.categories.application

import CreateCategoryRequest
import hyuuny.fooddelivery.categories.domain.Category
import hyuuny.fooddelivery.categories.infrastructure.CategoryRepository
import hyuuny.fooddelivery.common.constant.DeliveryType
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

internal class CreateCategoryUseCaseTest : BehaviorSpec({

    val repository = mockk<CategoryRepository>()
    val useCase = CategoryUseCase(repository)

    given("카테고리를") {
        val request = CreateCategoryRequest(
            deliveryType = DeliveryType.OUTSOURCING,
            name = "족발/보쌈",
            priority = 1,
            iconImageUrl = "pig-foot-image-url",
            visible = true
        )

        val now = LocalDateTime.now()
        val category = Category(
            id = 1,
            deliveryType = DeliveryType.OUTSOURCING,
            name = "족발/보쌈",
            priority = 1,
            iconImageUrl = "pig-foot-image-url",
            visible = true,
            createdAt = now,
            updatedAt = now
        )
        coEvery { repository.insert(any()) } returns category

        `when`("입력한 정보로") {
            val result = useCase.createCategory(request)

            then("등록할 수 있다.") {
                result.id.shouldNotBeNull()
                result.deliveryType shouldBe request.deliveryType
                result.name shouldBe request.name
                result.priority shouldBe request.priority
                result.iconImageUrl shouldBe request.iconImageUrl
                result.visible shouldBe request.visible
                result.createdAt.shouldNotBeNull()
                result.updatedAt.shouldNotBeNull()
            }
        }
    }
})
