package hyuuny.fooddelivery.options.application

import hyuuny.fooddelivery.options.domain.Option
import hyuuny.fooddelivery.options.infrastructure.OptionRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

internal class GetOptionUseCaseTest : BehaviorSpec({

    val repository = mockk<OptionRepository>()
    val useCase = OptionUseCase(repository)

    given("옵션을 상세조회 할 때") {
        val id = 1L
        val now = LocalDateTime.now()
        val option = Option(
            id = id,
            optionGroupId = 1L,
            name = "싸이버거",
            price = 6000,
            createdAt = now,
            updatedAt = now
        )
        coEvery { repository.findById(any()) } returns option

        `when`("존재하는 아이디이면") {
            val result = useCase.getOption(id)

            then("옵션을 상세조회 할 수 있다.") {
                result.id shouldBe id
                result.optionGroupId shouldBe option.optionGroupId
                result.name shouldBe option.name
                result.price shouldBe option.price
                result.createdAt shouldBe option.createdAt
                result.updatedAt shouldBe option.updatedAt
            }
        }

        `when`("존재하지 않는 아이디이면") {
            coEvery { repository.findById(any()) } returns null

            then("옵션을 상세조회 할 수 없다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.getOption(0)
                }
                ex.message shouldBe "0번 옵션을 찾을 수 없습니다."
            }
        }
    }
})
