package hyuuny.fooddelivery.categories.presentation.admin.v1

import CreateCategoryRequest
import ReOrderCategoryRequest
import ReOrderCategoryRequests
import UpdateCategoryRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.BaseIntegrationTest
import hyuuny.fooddelivery.categories.application.CategoryUseCase
import hyuuny.fooddelivery.categories.domain.Category
import hyuuny.fooddelivery.categories.infrastructure.CategoryRepository
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.DeliveryType.OUTSOURCING
import hyuuny.fooddelivery.common.constant.DeliveryType.SELF
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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
            deliveryType = OUTSOURCING,
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
            deliveryType = OUTSOURCING,
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

    @DisplayName("카테고리 목록을 조회할 수 있다.")
    @Test
    fun getCategories() {
        val now = LocalDateTime.now()
        val firstCategory = Category(
            id = 1,
            deliveryType = OUTSOURCING,
            name = "족발/보쌈",
            priority = 1,
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
            deliveryType = DeliveryType.TAKE_OUT,
            name = "치킨",
            priority = 3,
            iconImageUrl = "chicken-image-url",
            visible = true,
            createdAt = now,
            updatedAt = now
        )

        val fourthCategory = Category(
            id = 4,
            deliveryType = OUTSOURCING,
            name = "피자",
            priority = 4,
            iconImageUrl = "pizza-image-url",
            visible = false,
            createdAt = now,
            updatedAt = now
        )

        val fifthCategory = Category(
            id = 5,
            deliveryType = SELF,
            name = "버거",
            priority = 5,
            iconImageUrl = "burger-image-url",
            visible = false,
            createdAt = now,
            updatedAt = now
        )
        val categories = listOf(firstCategory, secondCategory, thirdCategory, fourthCategory, fifthCategory)
            .sortedByDescending { it.id }

        val pageable = PageRequest.of(0, 15, Sort.by(Sort.DEFAULT_DIRECTION, "id"))
        val page = PageImpl(categories, pageable, categories.size.toLong())
        coEvery { useCase.getCategoriesByAdminCondition(any(), any()) } returns page

        webTestClient.get().uri("/admin/v1/categories?sort:id:desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content[0].id").isEqualTo(fifthCategory.id!!)
            .jsonPath("$.content[0].deliveryType").isEqualTo(fifthCategory.deliveryType.name)
            .jsonPath("$.content[0].name").isEqualTo(fifthCategory.name)
            .jsonPath("$.content[0].priority").isEqualTo(fifthCategory.priority)
            .jsonPath("$.content[0].visible").isEqualTo(fifthCategory.visible)
            .jsonPath("$.content[0].createdAt").exists()
            .jsonPath("$.content[1].id").isEqualTo(fourthCategory.id!!)
            .jsonPath("$.content[1].deliveryType").isEqualTo(fourthCategory.deliveryType.name)
            .jsonPath("$.content[1].name").isEqualTo(fourthCategory.name)
            .jsonPath("$.content[1].priority").isEqualTo(fourthCategory.priority)
            .jsonPath("$.content[1].visible").isEqualTo(fourthCategory.visible)
            .jsonPath("$.content[1].createdAt").exists()
            .jsonPath("$.content[2].id").isEqualTo(thirdCategory.id!!)
            .jsonPath("$.content[2].deliveryType").isEqualTo(thirdCategory.deliveryType.name)
            .jsonPath("$.content[2].name").isEqualTo(thirdCategory.name)
            .jsonPath("$.content[2].priority").isEqualTo(thirdCategory.priority)
            .jsonPath("$.content[2].visible").isEqualTo(thirdCategory.visible)
            .jsonPath("$.content[2].createdAt").exists()
            .jsonPath("$.content[3].id").isEqualTo(secondCategory.id!!)
            .jsonPath("$.content[3].deliveryType").isEqualTo(secondCategory.deliveryType.name)
            .jsonPath("$.content[3].name").isEqualTo(secondCategory.name)
            .jsonPath("$.content[3].priority").isEqualTo(secondCategory.priority)
            .jsonPath("$.content[3].visible").isEqualTo(secondCategory.visible)
            .jsonPath("$.content[3].createdAt").exists()
            .jsonPath("$.content[4].id").isEqualTo(firstCategory.id!!)
            .jsonPath("$.content[4].deliveryType").isEqualTo(firstCategory.deliveryType.name)
            .jsonPath("$.content[4].name").isEqualTo(firstCategory.name)
            .jsonPath("$.content[4].priority").isEqualTo(firstCategory.priority)
            .jsonPath("$.content[4].visible").isEqualTo(firstCategory.visible)
            .jsonPath("$.content[4].createdAt").exists()
            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(5)
    }

    @DisplayName("카테고리를 수정할 수 있다.")
    @Test
    fun updateCategory() {
        val request = UpdateCategoryRequest(
            deliveryType = OUTSOURCING,
            name = "피자",
            iconImageUrl = "icon-image-url.jpg",
            visible = false,
        )
        coEvery { useCase.updateCategory(any(), any()) } returns Unit

        webTestClient.put().uri("/admin/v1/categories/${1}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("카테고리의 순서를 변경할 수 있다.")
    @Test
    fun reOrderCategories() {
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
        coEvery { repository.findAllCategoriesByDeliveryType(any()) } returns categories

        val requests = ReOrderCategoryRequests(
            reOrderedCategories = listOf(
                ReOrderCategoryRequest(3, 1),
                ReOrderCategoryRequest(1, 2),
                ReOrderCategoryRequest(2, 3),
            )
        )
        coEvery { useCase.reOrderCategories(DeliveryType.TAKE_OUT, requests) } returns Unit

        webTestClient.patch().uri("/admin/v1/categories/delivery-type/${DeliveryType.TAKE_OUT}/re-order")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requests)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("우선 순위에 맞춰 정렬된 노출 카테고리 목록을 조회할 수 있다.")
    @Test
    fun getVisibleCategoriesByDeliveryTypeOrderByPriority() {
        val now = LocalDateTime.now()
        val firstCategory = Category(
            id = 1,
            deliveryType = OUTSOURCING,
            name = "족발/보쌈",
            priority = 2,
            iconImageUrl = "pig-foot-image-url",
            visible = true,
            createdAt = now,
            updatedAt = now
        )

        val secondCategory = Category(
            id = 2,
            deliveryType = OUTSOURCING,
            name = "돈까스/회/일식",
            priority = 3,
            iconImageUrl = "pork-cutlet-image-url",
            visible = false,
            createdAt = now,
            updatedAt = now
        )

        val thirdCategory = Category(
            id = 3,
            deliveryType = OUTSOURCING,
            name = "치킨",
            priority = 5,
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
            visible = true,
            createdAt = now,
            updatedAt = now
        )

        val fifthCategory = Category(
            id = 5,
            deliveryType = OUTSOURCING,
            name = "버거",
            priority = 4,
            iconImageUrl = "burger-image-url",
            visible = false,
            createdAt = now,
            updatedAt = now
        )
        val categories = listOf(firstCategory, secondCategory, thirdCategory, fourthCategory, fifthCategory)
            .filter { it.visible }.sortedBy { it.priority }
        coEvery { useCase.getVisibleCategoriesByDeliveryTypeOrderByPriority(OUTSOURCING) } returns categories

        webTestClient.get().uri("/admin/v1/categories/delivery-type/$OUTSOURCING")
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
            .jsonPath("$.[0].createdAt").exists()
            .jsonPath("$.[1].id").isEqualTo(firstCategory.id!!)
            .jsonPath("$.[1].deliveryType").isEqualTo(firstCategory.deliveryType.name)
            .jsonPath("$.[1].name").isEqualTo(firstCategory.name)
            .jsonPath("$.[1].priority").isEqualTo(firstCategory.priority)
            .jsonPath("$.[1].visible").isEqualTo(firstCategory.visible)
            .jsonPath("$.[1].createdAt").exists()
            .jsonPath("$.[2].id").isEqualTo(thirdCategory.id!!)
            .jsonPath("$.[2].deliveryType").isEqualTo(thirdCategory.deliveryType.name)
            .jsonPath("$.[2].name").isEqualTo(thirdCategory.name)
            .jsonPath("$.[2].priority").isEqualTo(thirdCategory.priority)
            .jsonPath("$.[2].visible").isEqualTo(thirdCategory.visible)
            .jsonPath("$.[2].createdAt").exists()
    }

    @DisplayName("카테고리를 삭제할 수 있다.")
    @Test
    fun deleteCategory() {
        coEvery { useCase.deleteCategory(any()) } returns Unit

        webTestClient.delete().uri("/admin/v1/categories/${1}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
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
