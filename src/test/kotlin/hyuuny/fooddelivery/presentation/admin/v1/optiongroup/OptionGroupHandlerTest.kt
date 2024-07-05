package hyuuny.fooddelivery.presentation.admin.v1.optiongroup

import CreateOptionGroupRequest
import ReorderOptionGroupRequest
import ReorderOptionGroupRequests
import UpdateOptionGroupRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.application.optiongroup.OptionGroupUseCase
import hyuuny.fooddelivery.common.constant.MenuStatus
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.domain.optiongroup.OptionGroup
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import java.time.LocalDateTime

class OptionGroupHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: OptionGroupUseCase

    @MockkBean
    private lateinit var menuUseCase: MenuUseCase

    @DisplayName("메뉴의 옵션그룹을 등록할 수 있다.")
    @Test
    fun createOptionGroup() {
        val menuId = 130L

        val now = LocalDateTime.now()
        val menu = Menu(
            id = menuId,
            menuGroupId = 1L,
            name = "싸이버거",
            price = 6000,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "cyburger-image-url",
            description = "[베스트]닭다리살",
            createdAt = now,
            updatedAt = now
        )
        val request = CreateOptionGroupRequest(
            menuId = menuId,
            name = "치킨세트",
            required = true,
            priority = 1,
        )
        val optionGroup = generateOptionGroup(menuId, request)
        coEvery { menuUseCase.getMenu(any()) } returns menu
        coEvery { useCase.createOptionGroup(any(), any()) } returns optionGroup

        webTestClient.post().uri("/admin/v1/option-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(optionGroup.id!!)
            .jsonPath("$.menuId").isEqualTo(optionGroup.menuId)
            .jsonPath("$.name").isEqualTo(optionGroup.name)
            .jsonPath("$.required").isEqualTo(optionGroup.required)
            .jsonPath("$.priority").isEqualTo(optionGroup.priority)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    @DisplayName("옵션그룹을 상세조회 할 수 있다.")
    @Test
    fun getOptionGroup() {
        val menuId = 130L

        val request = CreateOptionGroupRequest(
            menuId = menuId,
            name = "치킨세트",
            required = true,
            priority = 1,
        )
        val optionGroup = generateOptionGroup(menuId, request)
        coEvery { useCase.getOptionGroup(any()) } returns optionGroup

        webTestClient.get().uri("/admin/v1/option-groups/${optionGroup.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(optionGroup.id!!)
            .jsonPath("$.menuId").isEqualTo(optionGroup.menuId)
            .jsonPath("$.name").isEqualTo(optionGroup.name)
            .jsonPath("$.required").isEqualTo(optionGroup.required)
            .jsonPath("$.priority").isEqualTo(optionGroup.priority)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }


    @DisplayName("옵션그룹 목록을 불러올 수 있다.")
    @Test
    fun getOptionGroups() {
        val now = LocalDateTime.now()
        val firstOptionGroup = OptionGroup(
            id = 1L,
            menuId = 1L,
            name = "치킨세트",
            required = true,
            priority = 1,
            createdAt = now,
            updatedAt = now
        )
        val secondOptionGroup = OptionGroup(
            id = 2L,
            menuId = 1L,
            name = "사장님 추천",
            required = true,
            priority = 2,
            createdAt = now,
            updatedAt = now
        )
        val thirdOptionGroup = OptionGroup(
            id = 3L,
            menuId = 2L,
            name = "사이드!",
            required = true,
            priority = 3,
            createdAt = now,
            updatedAt = now
        )
        val optionGroups = listOf(firstOptionGroup, secondOptionGroup, thirdOptionGroup).sortedByDescending { it.id }

        val pageable = PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "id"))
        val page = PageImpl(optionGroups, pageable, optionGroups.size.toLong())
        coEvery { useCase.getOptionGroupsByAdminCondition(any(), any()) } returns page

        webTestClient.get().uri("/admin/v1/option-groups?menu_id=&name=&sort=id:desc")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content[0].id").isEqualTo(thirdOptionGroup.id!!)
            .jsonPath("$.content[0].menuId").isEqualTo(thirdOptionGroup.menuId)
            .jsonPath("$.content[0].name").isEqualTo(thirdOptionGroup.name)
            .jsonPath("$.content[0].required").isEqualTo(thirdOptionGroup.required)
            .jsonPath("$.content[0].priority").isEqualTo(thirdOptionGroup.priority)
            .jsonPath("$.content[0].createdAt").exists()
            .jsonPath("$.content[1].id").isEqualTo(secondOptionGroup.id!!)
            .jsonPath("$.content[1].menuId").isEqualTo(secondOptionGroup.menuId)
            .jsonPath("$.content[1].name").isEqualTo(secondOptionGroup.name)
            .jsonPath("$.content[1].required").isEqualTo(secondOptionGroup.required)
            .jsonPath("$.content[1].priority").isEqualTo(secondOptionGroup.priority)
            .jsonPath("$.content[1].createdAt").exists()
            .jsonPath("$.content[2].id").isEqualTo(firstOptionGroup.id!!)
            .jsonPath("$.content[2].menuId").isEqualTo(firstOptionGroup.menuId)
            .jsonPath("$.content[2].name").isEqualTo(firstOptionGroup.name)
            .jsonPath("$.content[2].required").isEqualTo(firstOptionGroup.required)
            .jsonPath("$.content[2].priority").isEqualTo(firstOptionGroup.priority)
            .jsonPath("$.content[2].createdAt").exists()
            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(3)
    }

    @DisplayName("메뉴 아이디로 검색하면 메뉴아이디에 해당하는 옵션그룹 목록을 불러올 수 있다.")
    @Test
    fun getOptionGroups_by_optionId() {
        val now = LocalDateTime.now()
        val firstOptionGroup = OptionGroup(
            id = 1L,
            menuId = 1L,
            name = "치킨세트",
            required = true,
            priority = 1,
            createdAt = now,
            updatedAt = now
        )
        val secondOptionGroup = OptionGroup(
            id = 2L,
            menuId = 1L,
            name = "사장님 추천",
            required = true,
            priority = 2,
            createdAt = now,
            updatedAt = now
        )
        val optionGroups = listOf(firstOptionGroup, secondOptionGroup).sortedByDescending { it.id }

        val pageable = PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "id"))
        val page = PageImpl(optionGroups, pageable, optionGroups.size.toLong())
        coEvery { useCase.getOptionGroupsByAdminCondition(any(), any()) } returns page

        webTestClient.get().uri("/admin/v1/option-groups?menu_id=1&name=&sort=id:desc")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content[0].id").isEqualTo(secondOptionGroup.id!!)
            .jsonPath("$.content[0].menuId").isEqualTo(secondOptionGroup.menuId)
            .jsonPath("$.content[0].name").isEqualTo(secondOptionGroup.name)
            .jsonPath("$.content[0].required").isEqualTo(secondOptionGroup.required)
            .jsonPath("$.content[0].priority").isEqualTo(secondOptionGroup.priority)
            .jsonPath("$.content[0].createdAt").exists()
            .jsonPath("$.content[1].id").isEqualTo(firstOptionGroup.id!!)
            .jsonPath("$.content[1].menuId").isEqualTo(firstOptionGroup.menuId)
            .jsonPath("$.content[1].name").isEqualTo(firstOptionGroup.name)
            .jsonPath("$.content[1].required").isEqualTo(firstOptionGroup.required)
            .jsonPath("$.content[1].priority").isEqualTo(firstOptionGroup.priority)
            .jsonPath("$.content[1].createdAt").exists()
            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(2)
    }

    @DisplayName("옵션그룹의 정보를 변경할 수 있다.")
    @Test
    fun updateOptionGroup() {
        val id = 1L
        val request = UpdateOptionGroupRequest(
            menuId = 1L,
            name = "치킨세트",
            required = true
        )
        coEvery { useCase.updateOptionGroup(any(), any()) } returns Unit

        webTestClient.put().uri("/admin/v1/option-groups/$id")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("옵션그룹의 순서를 변경할 수 있다.")
    @Test
    fun reOrderOptionGroup() {
        val menuId = 1L
        val requests = ReorderOptionGroupRequests(
            menuId = menuId,
            reOrderedOptionGroups = listOf(
                ReorderOptionGroupRequest(
                    optionGroupId = menuId,
                    priority = 3,
                ),
                ReorderOptionGroupRequest(
                    optionGroupId = menuId,
                    priority = 1,
                ),
                ReorderOptionGroupRequest(
                    optionGroupId = menuId,
                    priority = 2,
                )
            ),
        )
        coEvery { useCase.reOrderOptionGroups(any(), any()) } returns Unit

        webTestClient.patch().uri("/admin/v1/option-groups/re-order")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requests)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("옵션그룹을 삭제할 수 있다.")
    @Test
    fun deleteOptionGroup() {
        val id = 1L
        coEvery { useCase.deleteOptionGroup(any()) } returns Unit

        webTestClient.delete().uri("/admin/v1/option-groups/$id")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    private fun generateOptionGroup(menuId: Long, request: CreateOptionGroupRequest): OptionGroup {
        val now = LocalDateTime.now()
        return OptionGroup(
            id = 1L,
            menuId = menuId,
            name = request.name,
            required = request.required,
            priority = request.priority,
            createdAt = now,
            updatedAt = now
        )
    }
}
