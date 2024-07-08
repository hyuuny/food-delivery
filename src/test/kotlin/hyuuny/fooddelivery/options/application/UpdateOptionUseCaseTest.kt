package hyuuny.fooddelivery.options.application

import UpdateOptionRequest
import hyuuny.fooddelivery.options.domain.Option
import hyuuny.fooddelivery.options.infrastructure.OptionRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class UpdateOptionUseCaseTest : BehaviorSpec({

    val repository = mockk<OptionRepository>()
    val useCase = OptionUseCase(repository)

    Given("메뉴 옵션을 수정할 때") {
        val id = 1L
        val now = LocalDateTime.now()
        val option = Option(
            id = id,
            optionGroupId = 1L,
            name = "후라이드 + 양념",
            price = 1000,
            createdAt = now,
            updatedAt = now,
        )
        val request = UpdateOptionRequest(
            name = "불닭 + 청양마요",
            price = 3000,
        )
        coEvery { repository.findById(any()) } returns option
        coEvery { repository.update(any()) } returns Unit

        `when`("옵션명이 공백이 아니라면") {
            useCase.updateOption(id, request)

            then("메뉴 옵션을 수정할 수 있다.") {
                coEvery { repository.update(any()) }
            }
        }

        `when`("옵션명을 입력하지 않고 공백으로 제출하면") {
            coEvery { repository.findById(any()) } returns option

            then("메뉴 옵션을 수정할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.updateOption(id, UpdateOptionRequest(name = "", price = 3000))
                }
                ex.message shouldBe "옵션명은 공백일수 없습니다."
            }
        }
    }

})
