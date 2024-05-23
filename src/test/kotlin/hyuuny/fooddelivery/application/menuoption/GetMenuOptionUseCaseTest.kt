package hyuuny.fooddelivery.application.menuoption

import hyuuny.fooddelivery.domain.menuoption.MenuOption
import hyuuny.fooddelivery.infrastructure.menuoption.MenuOptionRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

internal class GetMenuOptionUseCaseTest : BehaviorSpec({

    val repository = mockk<MenuOptionRepository>()
    val useCase = MenuOptionUseCase(repository)

    given("메뉴옵션을 상세조회 할 때") {
        val id = 1L
        val now = LocalDateTime.now()
        val menuOption = MenuOption(
            id = id,
            menuGroupId = 1L,
            name = "싸이버거",
            price = 6000,
            createdAt = now,
            updatedAt = now
        )
        coEvery { repository.findById(any()) } returns menuOption

        `when`("존재하는 아이디이면") {
            val result = useCase.getMenuOption(id)

            then("메뉴옵션을 상세조회 할 수 있다.") {
                result.id shouldBe id
                result.menuGroupId shouldBe menuOption.menuGroupId
                result.name shouldBe menuOption.name
                result.price shouldBe menuOption.price
                result.createdAt shouldBe menuOption.createdAt
                result.updatedAt shouldBe menuOption.updatedAt
            }
        }

        `when`("존재하지 않는 아이디이면") {
            coEvery { repository.findById(any()) } returns null

            then("메뉴옵션을 상세조회 할 수 없다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.getMenuOption(0)
                }
                ex.message shouldBe "메뉴옵션을 찾을 수 없습니다."
            }
        }
    }
})