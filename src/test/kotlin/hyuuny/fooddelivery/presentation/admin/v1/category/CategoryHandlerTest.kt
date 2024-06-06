package hyuuny.fooddelivery.presentation.admin.v1.category

import CreateCategoryRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.category.CategoryUseCase
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.domain.Category
import hyuuny.fooddelivery.infrastructure.category.CategoryRepository
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.time.LocalDateTime

class CategoryHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: CategoryUseCase

    @MockkBean
    private lateinit var repository: CategoryRepository

    @DisplayName("카테고리를 등록할 수 있다.")
    @Test
    fun createCategory() {
        val request = CreateCategoryRequest(
            deliveryType = DeliveryType.OUTSOURCING,
            name = "족발/보쌈",
            priority = 1,
            iconImageUrl = "pig-foot-image-url",
            visible = true
        )
        val category = generateCategory(request)
        coEvery { useCase.createCategory(any()) } returns category

        webTestClient.post().uri("/admin/v1/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(category.id!!)
            .jsonPath("$.deliveryType").isEqualTo(category.deliveryType.name)
            .jsonPath("$.name").isEqualTo(category.name)
            .jsonPath("$.priority").isEqualTo(category.priority)
            .jsonPath("$.visible").isEqualTo(category.visible)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    @DisplayName("카테고리를 상세조회 할 수 있다.")
    @Test
    fun getCategory() {
        val request = CreateCategoryRequest(
            deliveryType = DeliveryType.OUTSOURCING,
            name = "족발/보쌈",
            priority = 1,
            iconImageUrl = "pig-foot-image-url",
            visible = true
        )
        val category = generateCategory(request)
        coEvery { useCase.getCategory(any()) } returns category

        webTestClient.get().uri("/admin/v1/categories/${category.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(category.id!!)
            .jsonPath("$.deliveryType").isEqualTo(category.deliveryType.name)
            .jsonPath("$.name").isEqualTo(category.name)
            .jsonPath("$.priority").isEqualTo(category.priority)
            .jsonPath("$.visible").isEqualTo(category.visible)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    private fun generateCategory(request: CreateCategoryRequest): Category {
        val now = LocalDateTime.now()
        return Category(
            id = 1,
            deliveryType = request.deliveryType,
            name = request.name,
            priority = request.priority,
            iconImageUrl = request.iconImageUrl,
            visible = request.visible,
            createdAt = now,
            updatedAt = now
        )
    }
}