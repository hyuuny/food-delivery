package hyuuny.fooddelivery.presentation.admin.v1.optiongroup

import CreateOptionGroupRequest
import ReorderOptionGroupRequest
import ReorderOptionGroupRequests
import UpdateOptionGroupRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.optiongroup.OptionGroupUseCase
import hyuuny.fooddelivery.domain.optiongroup.OptionGroup
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

class OptionGroupHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: OptionGroupUseCase

    @MockkBean
    private lateinit var menuRepository: MenuRepository

    @DisplayName("메뉴의 옵션그룹을 등록할 수 있다.")
    @Test
    fun createOptionGroup() {
        coEvery { menuRepository.existsById(any()) } returns true
        val request = CreateOptionGroupRequest(
            menuId = 1L,
            name = "치킨세트",
            required = true,
            priority = 1,
        )
        val optionGroup = generateOptionGroup(request)
        coEvery { useCase.createOptionGroup(request) } returns optionGroup

        webTestClient.post().uri("/admin/v1/menus/${request.menuId}/option-groups")
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

    @DisplayName("존재하지 메뉴의 옵션그룹은 등록할 수 없다.")
    @Test
    fun createOptionGroup_notFound() {
        coEvery { menuRepository.existsById(any()) } throws ResponseStatusException(NOT_FOUND, "존재하지 않는 메뉴입니다.")
        val request = CreateOptionGroupRequest(
            menuId = 0L,
            name = "치킨세트",
            required = true,
            priority = 1,
        )

        webTestClient.post().uri("/menus/${request.menuId}/option-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound
    }

    @DisplayName("옵션그룹을 상세조회 할 수 있다.")
    @Test
    fun getOptionGroup() {
        val request = CreateOptionGroupRequest(
            menuId = 1L,
            name = "치킨세트",
            required = true,
            priority = 1,
        )
        val optionGroup = generateOptionGroup(request)
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
        coEvery { useCase.getOptionGroups(any(), any()) } returns page

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
        coEvery { useCase.getOptionGroups(any(), any()) } returns page

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
        coEvery { menuRepository.existsById(any()) } returns true
        val request = UpdateOptionGroupRequest(
            menuId = 1L,
            name = "치킨세트",
            required = true
        )
        coEvery { useCase.updateOptionGroup(any(), any()) } returns Unit

        webTestClient.put().uri("/admin/v1/menus/${request.menuId}/option-groups/${1}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("존재하지 메뉴의 옵션그룹은 변경할 수 없다.")
    @Test
    fun updateOptionGroup_notFound() {
        coEvery { menuRepository.existsById(any()) } throws ResponseStatusException(NOT_FOUND, "존재하지 않는 메뉴입니다.")
        val request = UpdateOptionGroupRequest(
            menuId = 0L,
            name = "치킨세트",
            required = true
        )
        coEvery { useCase.updateOptionGroup(any(), any()) } returns Unit

        webTestClient.put().uri("/menus/${request.menuId}/option-groups${1}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound
    }

    @DisplayName("옵션그룹의 순서를 변경할 수 있다.")
    @Test
    fun reOrderOptionGroup() {
        coEvery { menuRepository.existsById(any()) } returns true

        val menuId = 1L
        val requests = ReorderOptionGroupRequests(
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

        webTestClient.patch().uri("/admin/v1/menus/${menuId}/option-groups/re-order")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requests)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("존재하지 않는 메뉴의 옵션그룹 순서를 변경할 수 없다.")
    @Test
    fun reOrderOptionGroup_notFound() {
        coEvery { menuRepository.existsById(any()) } throws ResponseStatusException(NOT_FOUND, "존재하지 않는 메뉴입니다.")

        val menuId = 0L
        val requests = ReorderOptionGroupRequests(
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

        webTestClient.patch().uri("/admin/v1/menus/${menuId}/option-groups/re-order")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requests)
            .exchange()
            .expectStatus().isNotFound
    }

    @DisplayName("옵션그룹을 삭제할 수 있다.")
    @Test
    fun deleteOptionGroup() {
        val menuId = 1
        coEvery { menuRepository.existsById(any()) } returns true
        coEvery { useCase.deleteOptionGroup(any()) } returns Unit

        webTestClient.delete().uri("/admin/v1/menus/${menuId}/option-groups/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("존재하지 않는 메뉴의 옵션그룹을 삭제할 수 없다.")
    @Test
    fun deleteOptionGroup_notFound() {
        val menuId = 1
        coEvery { menuRepository.existsById(any()) } throws ResponseStatusException(NOT_FOUND, "존재하지 않는 메뉴입니다.")
        coEvery { useCase.deleteOptionGroup(any()) } returns Unit

        webTestClient.delete().uri("/admin/v1/menus/${menuId}/option-groups/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }

    private fun generateOptionGroup(request: CreateOptionGroupRequest): OptionGroup {
        val now = LocalDateTime.now()
        return OptionGroup(
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
