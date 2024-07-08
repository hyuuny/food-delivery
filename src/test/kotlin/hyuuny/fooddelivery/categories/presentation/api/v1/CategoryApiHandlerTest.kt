package hyuuny.fooddelivery.categories.presentation.api.v1

import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.BaseIntegrationTest
import hyuuny.fooddelivery.categories.application.CategoryUseCase
import hyuuny.fooddelivery.categories.domain.Category
import hyuuny.fooddelivery.common.constant.DeliveryType
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.http.MediaType
import java.time.LocalDateTime

class CategoryApiHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: CategoryUseCase

    @DisplayName("사용자는 deliveryType별로 카테고리 목록을 조회할 수 있다.")
    @CsvSource("SELF", "OUTSOURCING", "TAKE_OUT")
    @ParameterizedTest
    fun getVisibleCategoriesByDeliveryTypeOrderByPriority(deliveryType: DeliveryType) {
        val now = LocalDateTime.now()
        val firstCategory = Category(
            id = 1,
            deliveryType = deliveryType,
            name = "족발/보쌈",
            priority = 2,
            iconImageUrl = "pig-foot-image-url",
            visible = true,
            createdAt = now,
            updatedAt = now
        )

        val secondCategory = Category(
            id = 2,
            deliveryType = deliveryType,
            name = "돈까스/회/일식",
            priority = 3,
            iconImageUrl = "pork-cutlet-image-url",
            visible = false,
            createdAt = now,
            updatedAt = now
        )

        val thirdCategory = Category(
            id = 3,
            deliveryType = deliveryType,
            name = "치킨",
            priority = 5,
            iconImageUrl = "chicken-image-url",
            visible = true,
            createdAt = now,
            updatedAt = now
        )

        val fourthCategory = Category(
            id = 4,
            deliveryType = deliveryType,
            name = "피자",
            priority = 1,
            iconImageUrl = "pizza-image-url",
            visible = true,
            createdAt = now,
            updatedAt = now
        )

        val fifthCategory = Category(
            id = 5,
            deliveryType = deliveryType,
            name = "버거",
            priority = 4,
            iconImageUrl = "burger-image-url",
            visible = false,
            createdAt = now,
            updatedAt = now
        )
        val categories = listOf(firstCategory, secondCategory, thirdCategory, fourthCategory, fifthCategory)
            .filter { it.visible }.sortedBy { it.priority }
        coEvery { useCase.getVisibleCategoriesByDeliveryTypeOrderByPriority(deliveryType) } returns categories

        webTestClient.get().uri("/api/v1/categories/delivery-type/$deliveryType")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.[0].id").isEqualTo(fourthCategory.id!!)
            .jsonPath("$.[0].deliveryType").isEqualTo(fourthCategory.deliveryType.name)
            .jsonPath("$.[0].name").isEqualTo(fourthCategory.name)
            .jsonPath("$.[0].priority").isEqualTo(fourthCategory.priority)
            .jsonPath("$.[0].visible").isEqualTo(fourthCategory.visible)
            .jsonPath("$.[1].id").isEqualTo(firstCategory.id!!)
            .jsonPath("$.[1].deliveryType").isEqualTo(firstCategory.deliveryType.name)
            .jsonPath("$.[1].name").isEqualTo(firstCategory.name)
            .jsonPath("$.[1].priority").isEqualTo(firstCategory.priority)
            .jsonPath("$.[1].visible").isEqualTo(firstCategory.visible)
            .jsonPath("$.[2].id").isEqualTo(thirdCategory.id!!)
            .jsonPath("$.[2].deliveryType").isEqualTo(thirdCategory.deliveryType.name)
            .jsonPath("$.[2].name").isEqualTo(thirdCategory.name)
            .jsonPath("$.[2].priority").isEqualTo(thirdCategory.priority)
            .jsonPath("$.[2].visible").isEqualTo(thirdCategory.visible)
    }
}
