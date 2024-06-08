package hyuuny.fooddelivery.application.category

import hyuuny.fooddelivery.common.constant.DeliveryType.OUTSOURCING
import hyuuny.fooddelivery.domain.Category
import hyuuny.fooddelivery.infrastructure.category.CategoryRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class GetVisibleCategoryUseCaseTest : BehaviorSpec({

    val repository = mockk<CategoryRepository>()
    val useCase = CategoryUseCase(repository)

    Given("배달유형으로 노출된 모든 카테고리를 조회하면") {
        val now = LocalDateTime.now()
        val firstCategory = Category(
            id = 1,
            deliveryType = OUTSOURCING,
            name = "족발/보쌈",
            priority = 5,
            iconImageUrl = "pig-foot-image-url",
            visible = true,
            createdAt = now,
            updatedAt = now
        )

        val secondCategory = Category(
            id = 2,
            deliveryType = OUTSOURCING,
            name = "돈까스/회/일식",
            priority = 2,
            iconImageUrl = "pork-cutlet-image-url",
            visible = true,
            createdAt = now,
            updatedAt = now
        )

        val thirdCategory = Category(
            id = 3,
            deliveryType = OUTSOURCING,
            name = "치킨",
            priority = 4,
            iconImageUrl = "chicken-image-url",
            visible = true,
            createdAt = now,
            updatedAt = now
        )

        val fourthCategory = Category(
            id = 4,
            deliveryType = OUTSOURCING,
            name = "피자",
            priority = 1,
            iconImageUrl = "pizza-image-url",
            visible = false,
            createdAt = now,
            updatedAt = now
        )

        val fifthCategory = Category(
            id = 5,
            deliveryType = OUTSOURCING,
            name = "버거",
            priority = 3,
            iconImageUrl = "burger-image-url",
            visible = false,
            createdAt = now,
            updatedAt = now
        )
        val categories = listOf(firstCategory, secondCategory, thirdCategory, fourthCategory, fifthCategory)
            .filter { it.visible }.sortedBy { it.priority }
        coEvery { repository.findAllCategoriesByDeliveryType(OUTSOURCING) } returns categories

        `when`("우선순위가 높은순(priority가 낮은)으로 정렬된") {
            val result = useCase.getVisibleCategoriesByDeliveryTypeOrderByPriority(OUTSOURCING)

            then("카테고리 목록을 조회할 수 있다.") {
                result[0].id shouldBe categories[0].id
                result[1].id shouldBe categories[1].id
                result[2].id shouldBe categories[2].id
                result.size shouldBe 3
            }
        }
    }

})