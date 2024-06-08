package hyuuny.fooddelivery.application.category

import ReOrderCategoryRequest
import ReOrderCategoryRequests
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.domain.category.Category
import hyuuny.fooddelivery.infrastructure.category.CategoryRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime

internal class ReOrderCategoryUseCaseTest : BehaviorSpec({

    val repository = mockk<CategoryRepository>()
    val useCase = CategoryUseCase(repository)

    given("카테고리 순서를 수정 할 때") {
        val now = LocalDateTime.now()
        val firstCategory = Category(
            id = 1,
            deliveryType = DeliveryType.TAKE_OUT,
            name = "족발/보쌈",
            priority = 1,
            iconImageUrl = "pig-foot-image-url",
            visible = true,
            createdAt = now,
            updatedAt = now
        )

        val secondCategory = Category(
            id = 2,
            deliveryType = DeliveryType.TAKE_OUT,
            name = "돈까스/회/일식",
            priority = 2,
            iconImageUrl = "pork-cutlet-image-url",
            visible = true,
            createdAt = now,
            updatedAt = now
        )

        val thirdCategory = Category(
            id = 3,
            deliveryType = DeliveryType.TAKE_OUT,
            name = "치킨",
            priority = 3,
            iconImageUrl = "chicken-image-url",
            visible = true,
            createdAt = now,
            updatedAt = now
        )
        val categories = listOf(firstCategory, secondCategory, thirdCategory)

        val requests = ReOrderCategoryRequests(
            reOrderedCategories = listOf(
                ReOrderCategoryRequest(3, 1),
                ReOrderCategoryRequest(1, 2),
                ReOrderCategoryRequest(2, 3),
            )
        )
        coEvery { repository.findAllCategoriesByDeliveryType(any()) } returns categories
        coEvery { repository.bulkUpdatePriority(any()) } returns Unit

        `when`("기존 카테고리와 개수가 일치하면") {
            useCase.reOrderCategories(DeliveryType.TAKE_OUT, requests)

            then("카테고리의 순서를 수정 할 수 있다.") {
                coVerify { repository.bulkUpdatePriority(any()) }
            }
        }

        `when`("기존 카테고리와 개수가 일치하지 않으면") {
            val incorrectRequests = ReOrderCategoryRequests(
                reOrderedCategories = listOf(
                    ReOrderCategoryRequest(3, 1),
                    ReOrderCategoryRequest(1, 2)
                )
            )
            coEvery { repository.findAllCategoriesByDeliveryType(any()) } returns categories

            then("카테고리의 순서를 수정할 수 없다.") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.reOrderCategories(DeliveryType.TAKE_OUT, incorrectRequests)
                }
                ex.message shouldBe "카테고리 개수가 일치하지 않습니다."
            }
        }
    }
})
