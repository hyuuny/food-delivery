package hyuuny.fooddelivery.presentation.admin.v1.menugroup

import CreateMenuGroupRequest
import ReorderMenuGroupRequest
import ReorderMenuGroupRequests
import UpdateMenuGroupRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.menugroup.MenuGroupUseCase
import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import java.time.LocalDateTime

class MenuGroupHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: MenuGroupUseCase

    @DisplayName("매장의 메뉴 그룹을 등록할 수 있다.")
    @Test
    fun createMenuGroup() {
        val storeId = 1L
        val request = CreateMenuGroupRequest(
            storeId = storeId,
            name = "추천메뉴",
            priority = 1,
            description = "자신있게 추천드려요!",
        )
        val menuGroup = generateMenuGroup(request)
        coEvery { useCase.createMenuGroup(any()) } returns menuGroup

        webTestClient.post().uri("/admin/v1/stores/${storeId}/menu-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(menuGroup.id!!)
            .jsonPath("$.storeId").isEqualTo(menuGroup.storeId)
            .jsonPath("$.name").isEqualTo(menuGroup.name)
            .jsonPath("$.priority").isEqualTo(menuGroup.priority)
            .jsonPath("$.description").isEqualTo(menuGroup.description!!)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    @DisplayName("매장 아이디가 일치하지 않으면, 메뉴 그룹을 등록할 수 없다.")
    @Test
    fun createMenuGroup_badRequest() {
        val storeId = 1L
        val request = CreateMenuGroupRequest(
            storeId = storeId,
            name = "추천메뉴",
            priority = 1,
            description = "자신있게 추천드려요!",
        )
        val menuGroup = generateMenuGroup(request)
        coEvery { useCase.createMenuGroup(any()) } returns menuGroup

        webTestClient.post().uri("/admin/v1/stores/${2}/menu-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
    }

    @DisplayName("메뉴그룹을 상세조회 할 수 있다.")
    @Test
    fun getMenuGroup() {
        val request = CreateMenuGroupRequest(
            storeId = 1L,
            name = "추천메뉴",
            priority = 1,
            description = "자신있게 추천드려요!",
        )
        val menuGroup = generateMenuGroup(request)
        coEvery { useCase.getMenuGroup(any()) } returns menuGroup

        webTestClient.get().uri("/admin/v1/menu-groups/${menuGroup.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(menuGroup.id!!)
            .jsonPath("$.storeId").isEqualTo(menuGroup.storeId)
            .jsonPath("$.name").isEqualTo(menuGroup.name)
            .jsonPath("$.priority").isEqualTo(menuGroup.priority)
            .jsonPath("$.description").isEqualTo(menuGroup.description!!)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    @DisplayName("메뉴그룹 목록을 조회할 수 있다.")
    @Test
    fun getMenuGroups() {
        val now = LocalDateTime.now()
        val firstMenuGroup = MenuGroup(
            id = 1L,
            storeId = 1L,
            name = "추천메뉴",
            priority = 1,
            description = "자신있게 추천드려요!",
            createdAt = now,
            updatedAt = now,
        )

        val secondMenuGroup = MenuGroup(
            id = 2L,
            storeId = 1L,
            name = "매니아층을 위한",
            priority = 1,
            createdAt = now,
            updatedAt = now,
        )

        val thirdMenuGroup = MenuGroup(
            id = 3L,
            storeId = 1L,
            name = "사이드 메뉴",
            priority = 1,
            description = "여러가지 사이드 메뉴",
            createdAt = now,
            updatedAt = now,
        )
        val menuGroups = listOf(firstMenuGroup, secondMenuGroup, thirdMenuGroup).sortedByDescending { it.id }

        val pageable = PageRequest.of(0, 15, Sort.by(Sort.DEFAULT_DIRECTION, "id"))
        val page = PageImpl(menuGroups, pageable, menuGroups.size.toLong())
        coEvery { useCase.getMenuGroupsByAdminCondition(any(), any()) } returns page

        webTestClient.get().uri("/admin/v1/menu-groups?sort:id:desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content[0].id").isEqualTo(thirdMenuGroup.id!!)
            .jsonPath("$.content[0].storeId").isEqualTo(thirdMenuGroup.storeId)
            .jsonPath("$.content[0].name").isEqualTo(thirdMenuGroup.name)
            .jsonPath("$.content[0].priority").isEqualTo(thirdMenuGroup.priority)
            .jsonPath("$.content[0].description").isEqualTo(thirdMenuGroup.description!!)
            .jsonPath("$.content[1].id").isEqualTo(secondMenuGroup.id!!)
            .jsonPath("$.content[1].storeId").isEqualTo(secondMenuGroup.storeId)
            .jsonPath("$.content[1].name").isEqualTo(secondMenuGroup.name)
            .jsonPath("$.content[1].priority").isEqualTo(secondMenuGroup.priority)
            .jsonPath("$.content[1].description").doesNotExist()
            .jsonPath("$.content[2].id").isEqualTo(firstMenuGroup.id!!)
            .jsonPath("$.content[2].storeId").isEqualTo(firstMenuGroup.storeId)
            .jsonPath("$.content[2].name").isEqualTo(firstMenuGroup.name)
            .jsonPath("$.content[2].priority").isEqualTo(firstMenuGroup.priority)
            .jsonPath("$.content[2].description").isEqualTo(firstMenuGroup.description!!)
            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(3)
    }

    @DisplayName("메뉴그룹의 정보를 변경할 수 있다.")
    @Test
    fun updateMenuGroup() {
        val storeId = 1L
        val request = UpdateMenuGroupRequest(
            name = "추천메뉴",
            description = "자신있게 추천드려요!",
        )
        coEvery { useCase.updateMenuGroup(any(), any()) } returns Unit

        webTestClient.put().uri("/admin/v1/stores/${storeId}/menu-groups/${1}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("메뉴그룹의 순서를 변경할 수 있다.")
    @Test
    fun reOrderMenuGroup() {
        val storeId = 1L
        val requests = ReorderMenuGroupRequests(
            reOrderedMenuGroups = listOf(
                ReorderMenuGroupRequest(menuGroupId = 2, priority = 1),
                ReorderMenuGroupRequest(menuGroupId = 3, priority = 2),
                ReorderMenuGroupRequest(menuGroupId = 1, priority = 3),
            )
        )
        coEvery { useCase.reOrderMenuGroups(storeId, requests) } returns Unit

        webTestClient.patch().uri("/admin/v1/stores/${storeId}/menu-groups/re-order")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requests)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("메뉴그릅을 삭제할 수 있다.")
    @Test
    fun deleteMenuGroup() {
        val id = 1L
        val storeId = 2L
        coEvery { useCase.deleteMenuGroup(any()) } returns Unit

        webTestClient.delete().uri("/admin/v1/stores/${storeId}/menu-groups/${id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    private fun generateMenuGroup(request: CreateMenuGroupRequest): MenuGroup {
        val now = LocalDateTime.now()
        return MenuGroup(
            id = 1,
            storeId = request.storeId,
            name = request.name,
            priority = request.priority,
            description = request.description,
            createdAt = now,
            updatedAt = now,
        )
    }

}
