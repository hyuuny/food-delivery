package hyuuny.fooddelivery.categories.application

import hyuuny.fooddelivery.categories.domain.Category
import hyuuny.fooddelivery.categories.infrastructure.CategoryRepository
import hyuuny.fooddelivery.common.constant.DeliveryType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class GetCategoryUseCaseTest : BehaviorSpec({

    val repository = mockk<CategoryRepository>()
    val useCase = CategoryUseCase(repository)

    Given("카테고리를 상세조회 할 때") {
        val categoryId = 1L
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
        coEvery { repository.findById(any()) } returns category

        `when`("존재하는 아이디이면") {
            val result = useCase.getCategory(categoryId)

            then("카테고리를 상세조회 할 수 있다.") {
                result.id.shouldNotBeNull()
                result.deliveryType shouldBe category.deliveryType
                result.name shouldBe category.name
                result.priority shouldBe category.priority
                result.iconImageUrl shouldBe category.iconImageUrl
                result.visible shouldBe category.visible
                result.createdAt.shouldNotBeNull()
                result.updatedAt.shouldNotBeNull()
            }
        }

        `when`("존재하지 않는 아이디이면") {
            coEvery { repository.findById(any()) } returns null

            then("카테고리를 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.getCategory(0)
                }
                ex.message shouldBe "0번 카테고리를 찾을 수 없습니다."
            }
        }
    }

})