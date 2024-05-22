package hyuuny.fooddelivery.presentation.admin.v1.menugroup

import CreateMenuGroupRequest
import ReorderMenuGroupRequest
import ReorderMenuGroupRequests
import UpdateMenuGroupRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.menugroup.MenuGroupUseCase
import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import hyuuny.fooddelivery.infrastructure.menu.MenuRepository
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

class MenuGroupHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: MenuGroupUseCase

    @MockkBean
    private lateinit var menuRepository: MenuRepository

    @DisplayName("메뉴의 메뉴그룹을 등록할 수 있다.")
    @Test
    fun createMenuGroup() {
        coEvery { menuRepository.existsById(any()) } returns true
        val request = CreateMenuGroupRequest(
            menuId = 1L,
            name = "치킨세트",
            required = true,
            priority = 1,
        )
        val menuGroup = generateMenuGroup(request)
        coEvery { useCase.createMenuGroup(request) } returns menuGroup

        webTestClient.post().uri("/v1/menus/${request.menuId}/menu-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(menuGroup.id!!)
            .jsonPath("$.menuId").isEqualTo(menuGroup.menuId)
            .jsonPath("$.name").isEqualTo(menuGroup.name)
            .jsonPath("$.required").isEqualTo(menuGroup.required)
            .jsonPath("$.priority").isEqualTo(menuGroup.priority)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    @DisplayName("존재하지 메뉴의 메뉴그룹은 등록할 수 없다.")
    @Test
    fun createMenuGroup_notFound() {
        coEvery { menuRepository.existsById(any()) } throws ResponseStatusException(NOT_FOUND, "존재하지 않는 메뉴입니다.")
        val request = CreateMenuGroupRequest(
            menuId = 0L,
            name = "치킨세트",
            required = true,
            priority = 1,
        )

        webTestClient.post().uri("/menus/${request.menuId}/menu-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound
    }

    @DisplayName("메뉴그룹을 상세조회 할 수 있다.")
    @Test
    fun getMenuGroup() {
        val request = CreateMenuGroupRequest(
            menuId = 1L,
            name = "치킨세트",
            required = true,
            priority = 1,
        )
        val menuGroup = generateMenuGroup(request)
        coEvery { useCase.getMenuGroup(any()) } returns menuGroup

        webTestClient.get().uri("/v1/menu-groups/${menuGroup.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(menuGroup.id!!)
            .jsonPath("$.menuId").isEqualTo(menuGroup.menuId)
            .jsonPath("$.name").isEqualTo(menuGroup.name)
            .jsonPath("$.required").isEqualTo(menuGroup.required)
            .jsonPath("$.priority").isEqualTo(menuGroup.priority)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }


    @DisplayName("메뉴그룹 목록을 불러올 수 있다.")
    @Test
    fun getMenuGroups() {
        val now = LocalDateTime.now()
        val firstMenuGroup = MenuGroup(
            id = 1L,
            menuId = 1L,
            name = "치킨세트",
            required = true,
            priority = 1,
            createdAt = now,
            updatedAt = now
        )
        val secondMenuGroup = MenuGroup(
            id = 2L,
            menuId = 1L,
            name = "사장님 추천",
            required = true,
            priority = 2,
            createdAt = now,
            updatedAt = now
        )
        val thirdMenuGroup = MenuGroup(
            id = 3L,
            menuId = 2L,
            name = "사이드!",
            required = true,
            priority = 3,
            createdAt = now,
            updatedAt = now
        )
        val menuGroups = listOf(firstMenuGroup, secondMenuGroup, thirdMenuGroup).sortedByDescending { it.id }

        val pageable = PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "id"))
        val page = PageImpl(menuGroups, pageable, menuGroups.size.toLong())
        coEvery { useCase.getMenuGroups(any(), any()) } returns page

        webTestClient.get().uri("/v1/menu-groups?menu_id=&name=&sort=id:desc")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content[0].id").isEqualTo(thirdMenuGroup.id!!)
            .jsonPath("$.content[0].menuId").isEqualTo(thirdMenuGroup.menuId)
            .jsonPath("$.content[0].name").isEqualTo(thirdMenuGroup.name)
            .jsonPath("$.content[0].required").isEqualTo(thirdMenuGroup.required)
            .jsonPath("$.content[0].priority").isEqualTo(thirdMenuGroup.priority)
            .jsonPath("$.content[0].createdAt").exists()
            .jsonPath("$.content[1].id").isEqualTo(secondMenuGroup.id!!)
            .jsonPath("$.content[1].menuId").isEqualTo(secondMenuGroup.menuId)
            .jsonPath("$.content[1].name").isEqualTo(secondMenuGroup.name)
            .jsonPath("$.content[1].required").isEqualTo(secondMenuGroup.required)
            .jsonPath("$.content[1].priority").isEqualTo(secondMenuGroup.priority)
            .jsonPath("$.content[1].createdAt").exists()
            .jsonPath("$.content[2].id").isEqualTo(firstMenuGroup.id!!)
            .jsonPath("$.content[2].menuId").isEqualTo(firstMenuGroup.menuId)
            .jsonPath("$.content[2].name").isEqualTo(firstMenuGroup.name)
            .jsonPath("$.content[2].required").isEqualTo(firstMenuGroup.required)
            .jsonPath("$.content[2].priority").isEqualTo(firstMenuGroup.priority)
            .jsonPath("$.content[2].createdAt").exists()
            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(3)
    }

    @DisplayName("메뉴 아이디로 검색하면 메뉴아이디에 해당하는 메뉴그룹 목록을 불러올 수 있다.")
    @Test
    fun getMenuGroups_by_menuId() {
        val now = LocalDateTime.now()
        val firstMenuGroup = MenuGroup(
            id = 1L,
            menuId = 1L,
            name = "치킨세트",
            required = true,
            priority = 1,
            createdAt = now,
            updatedAt = now
        )
        val secondMenuGroup = MenuGroup(
            id = 2L,
            menuId = 1L,
            name = "사장님 추천",
            required = true,
            priority = 2,
            createdAt = now,
            updatedAt = now
        )
        val thirdMenuGroup = MenuGroup(
            id = 3L,
            menuId = 2L,
            name = "사이드!",
            required = true,
            priority = 3,
            createdAt = now,
            updatedAt = now
        )
        val menuGroups = listOf(firstMenuGroup, secondMenuGroup).sortedByDescending { it.id }

        val pageable = PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "id"))
        val page = PageImpl(menuGroups, pageable, menuGroups.size.toLong())
        coEvery { useCase.getMenuGroups(any(), any()) } returns page

        webTestClient.get().uri("/v1/menu-groups?menu_id=1&name=&sort=id:desc")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content[0].id").isEqualTo(secondMenuGroup.id!!)
            .jsonPath("$.content[0].menuId").isEqualTo(secondMenuGroup.menuId)
            .jsonPath("$.content[0].name").isEqualTo(secondMenuGroup.name)
            .jsonPath("$.content[0].required").isEqualTo(secondMenuGroup.required)
            .jsonPath("$.content[0].priority").isEqualTo(secondMenuGroup.priority)
            .jsonPath("$.content[0].createdAt").exists()
            .jsonPath("$.content[1].id").isEqualTo(firstMenuGroup.id!!)
            .jsonPath("$.content[1].menuId").isEqualTo(firstMenuGroup.menuId)
            .jsonPath("$.content[1].name").isEqualTo(firstMenuGroup.name)
            .jsonPath("$.content[1].required").isEqualTo(firstMenuGroup.required)
            .jsonPath("$.content[1].priority").isEqualTo(firstMenuGroup.priority)
            .jsonPath("$.content[1].createdAt").exists()
            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(2)
    }

    @DisplayName("메뉴그룹의 정보를 변경할 수 있다.")
    @Test
    fun updateMenuGroup() {
        coEvery { menuRepository.existsById(any()) } returns true
        val request = UpdateMenuGroupRequest(
            menuId = 1L,
            name = "치킨세트",
            required = true
        )
        coEvery { useCase.updateMenuGroup(any(), any()) } returns Unit

        webTestClient.put().uri("/v1/menus/${request.menuId}/menu-groups/${1}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("존재하지 메뉴의 메뉴그룹은 변경할 수 없다.")
    @Test
    fun updateMenuGroup_notFound() {
        coEvery { menuRepository.existsById(any()) } throws ResponseStatusException(NOT_FOUND, "존재하지 않는 메뉴입니다.")
        val request = UpdateMenuGroupRequest(
            menuId = 0L,
            name = "치킨세트",
            required = true
        )
        coEvery { useCase.updateMenuGroup(any(), any()) } returns Unit

        webTestClient.put().uri("/menus/${request.menuId}/menu-groups${1}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound
    }

    @DisplayName("메뉴그룹의 순서를 변경할 수 있다.")
    @Test
    fun reOrderMenuGroup() {
        coEvery { menuRepository.existsById(any()) } returns true

        val menuId = 1L
        val requests = ReorderMenuGroupRequests(
            reOrderedMenuGroups = listOf(
                ReorderMenuGroupRequest(
                    menuGroupId = menuId,
                    priority = 3,
                ),
                ReorderMenuGroupRequest(
                    menuGroupId = menuId,
                    priority = 1,
                ),
                ReorderMenuGroupRequest(
                    menuGroupId = menuId,
                    priority = 2,
                )
            ),
        )
        coEvery { useCase.reOrderMenuGroups(any(), any()) } returns Unit

        webTestClient.patch().uri("/v1/menus/${menuId}/menu-groups/re-order")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requests)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("존재하지 않는 메뉴의 메뉴그룹 순서를 변경할 수 없다.")
    @Test
    fun reOrderMenuGroup_notFound() {
        coEvery { menuRepository.existsById(any()) } throws ResponseStatusException(NOT_FOUND, "존재하지 않는 메뉴입니다.")

        val menuId = 0L
        val requests = ReorderMenuGroupRequests(
            reOrderedMenuGroups = listOf(
                ReorderMenuGroupRequest(
                    menuGroupId = menuId,
                    priority = 3,
                ),
                ReorderMenuGroupRequest(
                    menuGroupId = menuId,
                    priority = 1,
                ),
                ReorderMenuGroupRequest(
                    menuGroupId = menuId,
                    priority = 2,
                )
            ),
        )
        coEvery { useCase.reOrderMenuGroups(any(), any()) } returns Unit

        webTestClient.patch().uri("/v1/menus/${menuId}/menu-groups/re-order")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requests)
            .exchange()
            .expectStatus().isNotFound
    }

    @DisplayName("메뉴그룹을 삭제할 수 있다.")
    @Test
    fun deleteMenuGroup() {
        val menuId = 1
        coEvery { menuRepository.existsById(any()) } returns true
        coEvery { useCase.deleteMenuGroup(any()) } returns Unit

        webTestClient.delete().uri("/v1/menus/${menuId}/menu-groups/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("존재하지 않는 메뉴의 메뉴그룹을 삭제할 수 없다.")
    @Test
    fun deleteMenuGroup_notFound() {
        val menuId = 1
        coEvery { menuRepository.existsById(any()) } throws ResponseStatusException(NOT_FOUND, "존재하지 않는 메뉴입니다.")
        coEvery { useCase.deleteMenuGroup(any()) } returns Unit

        webTestClient.delete().uri("/v1/menus/${menuId}/menu-groups/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }

    private fun generateMenuGroup(request: CreateMenuGroupRequest): MenuGroup {
        val now = LocalDateTime.now()
        return MenuGroup(
            id = 1L,
            menuId = request.menuId,
            name = request.name,
            required = request.required,
            priority = request.priority,
            createdAt = now,
            updatedAt = now
        )
    }
}
