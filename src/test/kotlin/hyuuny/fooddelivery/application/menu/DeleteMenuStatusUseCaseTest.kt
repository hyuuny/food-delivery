package hyuuny.fooddelivery.application.menu

import hyuuny.fooddelivery.infrastructure.menu.MenuRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

internal class DeleteMenuStatusUseCaseTest : BehaviorSpec({

    val repository = mockk<MenuRepository>()
    val useCase = MenuUseCase(repository)

    given("메뉴를 삭제 할 때") {
        val menuId = 1L
        coEvery { repository.existsById(any()) } returns true
        coEvery { repository.delete(any()) } returns Unit

        `when`("존재하는 메뉴라면") {
            useCase.deleteMenu(menuId)

            then("정상적으로 메뉴를 삭제할 수 있다.") {
                coVerify { repository.delete(any()) }
            }
        }

        `when`("존재하지 않는 메뉴라면") {
            coEvery { repository.existsById(any()) } returns false

            then("존재하지 않는 메뉴라는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.deleteMenu(0)
                }
                ex.message shouldBe "존재하지 않는 메뉴입니다."
            }
        }
    }
})