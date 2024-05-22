package hyuuny.fooddelivery.application.menugroup

import hyuuny.fooddelivery.infrastructure.menugroup.MenuGroupRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

internal class DeleteMenuGroupUseCaseTest : BehaviorSpec({

    val repository = mockk<MenuGroupRepository>()
    val useCase = MenuGroupUseCase(repository)

    given("메뉴그룹을 삭제 할 때") {
        val menuGroupId = 1L
        coEvery { repository.existsById(any()) } returns true
        coEvery { repository.delete(any()) } returns Unit

        `when`("존재하는 메뉴그룹이면") {
            useCase.deleteMenuGroup(menuGroupId)

            then("정상적으로 메뉴그룹을 삭제할 수 있다.") {
                coVerify { repository.delete(any()) }
            }
        }

        `when`("존재하지 않는 메뉴그룹이면") {
            coEvery { repository.existsById(any()) } returns false

            then("메뉴그릅을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.deleteMenuGroup(0)
                }
                ex.message shouldBe "0번 메뉴그룹을 찾을 수 없습니다."
            }
        }
    }
})