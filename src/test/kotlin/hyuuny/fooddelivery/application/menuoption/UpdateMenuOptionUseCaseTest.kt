package hyuuny.fooddelivery.application.menuoption

import UpdateMenuOptionRequest
import hyuuny.fooddelivery.domain.menuoption.MenuOption
import hyuuny.fooddelivery.infrastructure.menuoption.MenuOptionRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class UpdateMenuOptionUseCaseTest : BehaviorSpec({

    val repository = mockk<MenuOptionRepository>()
    val useCase = MenuOptionUseCase(repository)

    Given("메뉴 옵션을 수정할 때") {
        val id = 1L
        val now = LocalDateTime.now()
        val menuOption = MenuOption(
            id = id,
            menuGroupId = 1L,
            name = "후라이드 + 양념",
            price = 1000,
            createdAt = now,
            updatedAt = now,
        )
        val request = UpdateMenuOptionRequest(
            name = "불닭 + 청양마요",
            price = 3000,
        )
        coEvery { repository.findById(any()) } returns menuOption
        coEvery { repository.update(any()) } returns Unit

        `when`("옵션명이 공백이 아니라면") {
            useCase.updateMenuOption(id, request)

            then("메뉴 옵션을 수정할 수 있다.") {
                coEvery { repository.update(any()) }
            }
        }

        `when`("옵션명을 입력하지 않고 공백으로 제출하면") {
            coEvery { repository.findById(any()) } returns menuOption

            then("메뉴 옵션을 수정할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.updateMenuOption(id, UpdateMenuOptionRequest(name = "", price = 3000))
                }
                ex.message shouldBe "옵션명은 공백일수 없습니다."
            }
        }
    }

})
