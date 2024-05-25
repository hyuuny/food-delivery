package hyuuny.fooddelivery.application.option

import CreateOptionRequest
import hyuuny.fooddelivery.domain.option.Option
import hyuuny.fooddelivery.infrastructure.option.OptionRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class CreateOptionUseCaseTest : BehaviorSpec({

    val repository = mockk<OptionRepository>()
    val useCase = OptionUseCase(repository)

    Given("옵션을 등록하면서") {
        val request = CreateOptionRequest(
            optionGroupId = 1L,
            name = "후라이드 + 양념",
            price = 1000
        )

        val now = LocalDateTime.now()
        val expectedOption = Option(
            id = 1,
            optionGroupId = 1L,
            name = "후라이드 + 양념",
            price = 1000,
            createdAt = now,
            updatedAt = now,
        )
        coEvery { repository.insert(any()) } returns expectedOption

        `when`("옵션명을 입력하면") {
            val result = useCase.createOption(request)

            then("옵션을 등록할 수 있다.") {
                result.id.shouldNotBeNull()
                result.optionGroupId shouldBe request.optionGroupId
                result.name shouldBe request.name
                result.price shouldBe request.price
                result.createdAt.shouldNotBeNull()
                result.updatedAt shouldBe result.createdAt
            }
        }

        `when`("옵션명을 입력하지 않고 공백으로 제출하면") {
            then("옵션을 등록할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createOption(
                        CreateOptionRequest(
                            optionGroupId = 1L,
                            name = "",
                            price = 1000
                        )
                    )
                }
                ex.message shouldBe "옵션명은 공백일수 없습니다."
            }
        }
    }

})