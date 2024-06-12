package hyuuny.fooddelivery.application.optiongroup

import hyuuny.fooddelivery.infrastructure.optiongroup.OptionGroupRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

internal class DeleteOptionGroupUseCaseTest : BehaviorSpec({

    val repository = mockk<OptionGroupRepository>()
    val useCase = OptionGroupUseCase(repository)

    given("옵션그룹을 삭제 할 때") {
        val optionGroupId = 1L
        coEvery { repository.existsById(any()) } returns true
        coEvery { repository.delete(any()) } returns Unit

        `when`("존재하는 옵션그룹이면") {
            useCase.deleteOptionGroup(optionGroupId)

            then("정상적으로 옵션그룹을 삭제할 수 있다.") {
                coVerify { repository.delete(any()) }
            }
        }

        `when`("존재하지 않는 옵션그룹이면") {
            coEvery { repository.existsById(any()) } returns false

            then("옵션그룹을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.deleteOptionGroup(0)
                }
                ex.message shouldBe "0번 옵션그룹을 찾을 수 없습니다."
            }
        }
    }
})