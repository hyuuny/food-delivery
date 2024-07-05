package hyuuny.fooddelivery.application.option

import CreateOptionRequest
import hyuuny.fooddelivery.application.optiongroup.OptionGroupUseCase
import hyuuny.fooddelivery.domain.option.Option
import hyuuny.fooddelivery.domain.optiongroup.OptionGroup
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
    val optionGroupUseCase = mockk<OptionGroupUseCase>()

    Given("옵션을 등록하면서") {
        val now = LocalDateTime.now()
        val optionGroup = OptionGroup(
            id = 13L,
            menuId = 3L,
            name = "추천 메뉴",
            required = true,
            priority = 1,
            createdAt = now,
            updatedAt = now
        )

        val request = CreateOptionRequest(
            optionGroupId = optionGroup.id!!,
            name = "후라이드 + 양념",
            price = 1000
        )
        val expectedOption = Option(
            id = 1,
            optionGroupId = optionGroup.id!!,
            name = "후라이드 + 양념",
            price = 1000,
            createdAt = now,
            updatedAt = now,
        )

        coEvery { optionGroupUseCase.getOptionGroup(any()) } returns optionGroup
        coEvery { repository.insert(any()) } returns expectedOption

        `when`("옵션명을 입력하면") {
            val result = useCase.createOption(request) { optionGroup }

            then("옵션을 등록할 수 있다.") {
                result.id.shouldNotBeNull()
                result.optionGroupId shouldBe optionGroup.id
                result.name shouldBe request.name
                result.price shouldBe request.price
                result.createdAt.shouldNotBeNull()
                result.updatedAt shouldBe result.createdAt
            }
        }

        `when`("옵션명을 입력하지 않고 공백으로 제출하면") {
            then("옵션을 등록할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createOption(CreateOptionRequest(optionGroup.id!!, "", 1000)) { optionGroup }
                }
                ex.message shouldBe "옵션명은 공백일수 없습니다."
            }
        }

        `when`("존재하지 않는 옵션그룹이면") {
            coEvery { optionGroupUseCase.getOptionGroup(any()) } throws NoSuchElementException("0번 옵션그룹을 찾을 수 없습니다.")

            then("옵션그룹을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.createOption(request) { optionGroupUseCase.getOptionGroup(0) }
                }
                ex.message shouldBe "0번 옵션그룹을 찾을 수 없습니다."
            }
        }
    }

})