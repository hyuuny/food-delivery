package hyuuny.fooddelivery.presentation.admin.v1.option

import ChangeOptionGroupIdRequest
import CreateOptionRequest
import UpdateOptionRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.option.OptionUseCase
import hyuuny.fooddelivery.application.optiongroup.OptionGroupUseCase
import hyuuny.fooddelivery.domain.option.Option
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

class OptionHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: OptionUseCase

    @MockkBean
    private lateinit var optionGroupUseCase: OptionGroupUseCase

    @DisplayName("옵션그룹에 옵션을 등록할 수 있다.")
    @Test
    fun createOption() {
        val optionGroupId = 1L

        val now = LocalDateTime.now()
        val optionGroup = OptionGroup(
            id = optionGroupId,
            menuId = 3L,
            name = "추천 메뉴",
            required = true,
            priority = 1,
            createdAt = now,
            updatedAt = now
        )
        val request = CreateOptionRequest(
            optionGroupId = optionGroupId,
            name = "후라이드 + 양념",
            price = 1000,
        )
        val option = generateOption(request)
        coEvery { optionGroupUseCase.getOptionGroup(any()) } returns optionGroup
        coEvery { useCase.createOption(any(), any()) } returns option

        webTestClient.post().uri("/admin/v1/options")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(option.id!!)
            .jsonPath("$.optionGroupId").isEqualTo(option.optionGroupId)
            .jsonPath("$.name").isEqualTo(option.name)
            .jsonPath("$.price").isEqualTo(option.price)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    @DisplayName("옵션 목록을 조회할 수 있다.")
    @Test
    fun getOptions() {
        val optionGroupId = 1L
        val now = LocalDateTime.now()
        val firstOption = Option(
            id = 1L,
            optionGroupId = optionGroupId,
            name = "후라이드 + 양념",
            price = 1000,
            createdAt = now,
            updatedAt = now
        )
        val secondOption = Option(
            id = 2L,
            optionGroupId = optionGroupId,
            name = "후라이드 + 간장",
            price = 1000,
            createdAt = now,
            updatedAt = now
        )
        val thirdOption = Option(
            id = 3L,
            optionGroupId = optionGroupId,
            name = "양념 + 간장",
            price = 2000,
            createdAt = now,
            updatedAt = now
        )
        val fourthOption = Option(
            id = 4L,
            optionGroupId = optionGroupId,
            name = "후라이드 + 크리미언",
            price = 2500,
            createdAt = now,
            updatedAt = now
        )
        val options = listOf(firstOption, secondOption, thirdOption, fourthOption).sortedByDescending { it.id }

        val pageable = PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "id"))
        val page = PageImpl(options, pageable, options.size.toLong())
        coEvery { useCase.getOptionsByAdminCondition(any(), any()) } returns page

        webTestClient.get().uri("/admin/v1/options?option-group-id=&name=&sort=id:desc")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content[0].id").isEqualTo(fourthOption.id!!)
            .jsonPath("$.content[0].optionGroupId").isEqualTo(fourthOption.optionGroupId)
            .jsonPath("$.content[0].name").isEqualTo(fourthOption.name)
            .jsonPath("$.content[0].price").isEqualTo(fourthOption.price)
            .jsonPath("$.content[0].createdAt").exists()
            .jsonPath("$.content[1].id").isEqualTo(thirdOption.id!!)
            .jsonPath("$.content[1].optionGroupId").isEqualTo(thirdOption.optionGroupId)
            .jsonPath("$.content[1].name").isEqualTo(thirdOption.name)
            .jsonPath("$.content[1].price").isEqualTo(thirdOption.price)
            .jsonPath("$.content[1].createdAt").exists()
            .jsonPath("$.content[2].id").isEqualTo(secondOption.id!!)
            .jsonPath("$.content[2].optionGroupId").isEqualTo(secondOption.optionGroupId)
            .jsonPath("$.content[2].name").isEqualTo(secondOption.name)
            .jsonPath("$.content[2].price").isEqualTo(secondOption.price)
            .jsonPath("$.content[2].createdAt").exists()
            .jsonPath("$.content[3].id").isEqualTo(firstOption.id!!)
            .jsonPath("$.content[3].optionGroupId").isEqualTo(firstOption.optionGroupId)
            .jsonPath("$.content[3].name").isEqualTo(firstOption.name)
            .jsonPath("$.content[3].price").isEqualTo(firstOption.price)
            .jsonPath("$.content[3].createdAt").exists()
            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(4)
    }

    @DisplayName("옵션의 가격은 오름차순, 아이디는 내림차순으로 목록을 조회할 수 있다.")
    @Test
    fun getOptions_sort_price_asc() {
        val optionGroupId = 1L
        val now = LocalDateTime.now()
        val firstOption = Option(
            id = 1L,
            optionGroupId = optionGroupId,
            name = "후라이드 + 양념",
            price = 2000,
            createdAt = now,
            updatedAt = now
        )
        val secondOption = Option(
            id = 2L,
            optionGroupId = optionGroupId,
            name = "후라이드 + 간장",
            price = 3000,
            createdAt = now,
            updatedAt = now
        )
        val thirdOption = Option(
            id = 3L,
            optionGroupId = optionGroupId,
            name = "양념 + 간장",
            price = 2000,
            createdAt = now,
            updatedAt = now
        )
        val fourthOption = Option(
            id = 4L,
            optionGroupId = optionGroupId,
            name = "후라이드 + 크리미언",
            price = 2500,
            createdAt = now,
            updatedAt = now
        )
        val options = listOf(firstOption, secondOption, thirdOption, fourthOption)
            .sortedWith(compareBy<Option> { it.price }.thenByDescending { it.id })

        val firstSort = Sort.Order(Sort.Direction.ASC, "price")
        val secondSort = Sort.Order(Sort.Direction.DESC, "id")
        val sort = Sort.by(listOf(firstSort, secondSort))

        val pageable = PageRequest.of(0, 15, sort)
        val page = PageImpl(options, pageable, options.size.toLong())
        coEvery { useCase.getOptionsByAdminCondition(any(), any()) } returns page

        webTestClient.get().uri("/admin/v1/options?option-group-id=&name=&sort=price:asc,id:desc")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content[0].id").isEqualTo(thirdOption.id!!)
            .jsonPath("$.content[0].optionGroupId").isEqualTo(thirdOption.optionGroupId)
            .jsonPath("$.content[0].name").isEqualTo(thirdOption.name)
            .jsonPath("$.content[0].price").isEqualTo(thirdOption.price)
            .jsonPath("$.content[0].createdAt").exists()
            .jsonPath("$.content[1].id").isEqualTo(firstOption.id!!)
            .jsonPath("$.content[1].optionGroupId").isEqualTo(firstOption.optionGroupId)
            .jsonPath("$.content[1].name").isEqualTo(firstOption.name)
            .jsonPath("$.content[1].price").isEqualTo(firstOption.price)
            .jsonPath("$.content[1].createdAt").exists()
            .jsonPath("$.content[2].id").isEqualTo(fourthOption.id!!)
            .jsonPath("$.content[2].optionGroupId").isEqualTo(fourthOption.optionGroupId)
            .jsonPath("$.content[2].name").isEqualTo(fourthOption.name)
            .jsonPath("$.content[2].price").isEqualTo(fourthOption.price)
            .jsonPath("$.content[2].createdAt").exists()
            .jsonPath("$.content[3].id").isEqualTo(secondOption.id!!)
            .jsonPath("$.content[3].optionGroupId").isEqualTo(secondOption.optionGroupId)
            .jsonPath("$.content[3].name").isEqualTo(secondOption.name)
            .jsonPath("$.content[3].price").isEqualTo(secondOption.price)
            .jsonPath("$.content[3].createdAt").exists()
            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(4)
    }

    @DisplayName("옵션을 상세조회 할 수 있다.")
    @Test
    fun getOption() {
        val optionGroupId = 1L

        val request = CreateOptionRequest(
            optionGroupId = optionGroupId,
            name = "후라이드 + 양념",
            price = 1000,
        )
        val option = generateOption(request)
        coEvery { useCase.getOption(any()) } returns option

        webTestClient.get().uri("/admin/v1/options/${option.id}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(option.id!!)
            .jsonPath("$.optionGroupId").isEqualTo(option.optionGroupId)
            .jsonPath("$.name").isEqualTo(option.name)
            .jsonPath("$.price").isEqualTo(option.price)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    @DisplayName("옵션의 정보를 변경할 수 있다.")
    @Test
    fun updateOption() {
        coEvery { optionGroupUseCase.existsById(any()) } returns true
        val request = UpdateOptionRequest(
            name = "불닭 + 청양마요",
            price = 3000,
        )
        coEvery { useCase.updateOption(any(), any()) } returns Unit

        webTestClient.put().uri("/admin/v1/options/1")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("옵션을 삭제할 수 있다.")
    @Test
    fun deleteOption() {
        coEvery { useCase.deleteOption(any()) } returns Unit

        webTestClient.delete().uri("/admin/v1/options/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("옵션의 옵션그룹을 변경할 수 있다.")
    @Test
    fun changeOptionGroup() {
        val id = 1L
        val request = ChangeOptionGroupIdRequest(optionGroupId = 2L)
        coEvery { useCase.changeOptionGroup(any(), any()) } returns Unit

        webTestClient.patch().uri("/admin/v1/options/${id}/change-option-group")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    private fun generateOption(request: CreateOptionRequest): Option {
        val now = LocalDateTime.now()
        return Option(
            id = 1L,
            optionGroupId = request.optionGroupId,
            name = request.name,
            price = request.price,
            createdAt = now,
            updatedAt = now
        )
    }
}