package hyuuny.fooddelivery.application.option

import hyuuny.fooddelivery.infrastructure.option.OptionRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

internal class DeleteOptionUseCaseTest : BehaviorSpec({

    val repository = mockk<OptionRepository>()
    val useCase = OptionUseCase(repository)

    given("옵션을 삭제 할 때") {
        val optionId = 1L
        coEvery { repository.existsById(any()) } returns true
        coEvery { repository.delete(any()) } returns Unit

        `when`("존재하는 옵션이라면") {
            useCase.deleteOption(optionId)

            then("정상적으로 옵션을 삭제할 수 있다.") {
                coVerify { repository.delete(any()) }
            }
        }

        `when`("존재하지 않는 옵션이라면") {
            coEvery { repository.existsById(any()) } returns false

            then("옵션을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.deleteOption(0)
                }
                ex.message shouldBe "옵션을 찾을 수 없습니다."
            }
        }
    }
})