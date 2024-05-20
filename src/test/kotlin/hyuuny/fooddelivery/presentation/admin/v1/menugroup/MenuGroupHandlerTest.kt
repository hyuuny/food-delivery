package hyuuny.fooddelivery.presentation.admin.v1.menugroup

import CreateMenuGroupRequest
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
            required = true
        )
        val menuGroup = generateMenuGroup(request)
        coEvery { useCase.createMenuGroup(request) } returns menuGroup

        webTestClient.post().uri("/menus/${request.menuId}/menu-groups")
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
            required = true
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
            required = true
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
            createdAt = now,
            updatedAt = now
        )
        val secondMenuGroup = MenuGroup(
            id = 2L,
            menuId = 1L,
            name = "사장님 추천",
            required = true,
            createdAt = now,
            updatedAt = now
        )
        val thirdMenuGroup = MenuGroup(
            id = 3L,
            menuId = 2L,
            name = "사이드!",
            required = true,
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
            .jsonPath("$.content[0].createdAt").exists()
            .jsonPath("$.content[1].id").isEqualTo(secondMenuGroup.id!!)
            .jsonPath("$.content[1].menuId").isEqualTo(secondMenuGroup.menuId)
            .jsonPath("$.content[1].name").isEqualTo(secondMenuGroup.name)
            .jsonPath("$.content[1].required").isEqualTo(secondMenuGroup.required)
            .jsonPath("$.content[1].createdAt").exists()
            .jsonPath("$.content[2].id").isEqualTo(firstMenuGroup.id!!)
            .jsonPath("$.content[2].menuId").isEqualTo(firstMenuGroup.menuId)
            .jsonPath("$.content[2].name").isEqualTo(firstMenuGroup.name)
            .jsonPath("$.content[2].required").isEqualTo(firstMenuGroup.required)
            .jsonPath("$.content[2].createdAt").exists()
            .jsonPath("$.totalElements").isEqualTo(3)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.number").isEqualTo(0)
            .jsonPath("$.last").isEqualTo(true)
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
            createdAt = now,
            updatedAt = now
        )
        val secondMenuGroup = MenuGroup(
            id = 2L,
            menuId = 1L,
            name = "사장님 추천",
            required = true,
            createdAt = now,
            updatedAt = now
        )
        val thirdMenuGroup = MenuGroup(
            id = 3L,
            menuId = 2L,
            name = "사이드!",
            required = true,
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
            .jsonPath("$.content[0].createdAt").exists()
            .jsonPath("$.content[1].id").isEqualTo(firstMenuGroup.id!!)
            .jsonPath("$.content[1].menuId").isEqualTo(firstMenuGroup.menuId)
            .jsonPath("$.content[1].name").isEqualTo(firstMenuGroup.name)
            .jsonPath("$.content[1].required").isEqualTo(firstMenuGroup.required)
            .jsonPath("$.content[1].createdAt").exists()
            .jsonPath("$.totalElements").isEqualTo(2)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.number").isEqualTo(0)
            .jsonPath("$.last").isEqualTo(true)
    }

    private fun generateMenuGroup(request: CreateMenuGroupRequest): MenuGroup {
        val now = LocalDateTime.now()
        return MenuGroup(
            id = 1L,
            menuId = request.menuId,
            name = request.name,
            required = request.required,
            createdAt = now,
            updatedAt = now
        )
    }
}
