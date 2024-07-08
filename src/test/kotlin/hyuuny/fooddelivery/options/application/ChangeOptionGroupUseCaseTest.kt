package hyuuny.fooddelivery.options.application

import hyuuny.fooddelivery.optiongroups.application.OptionGroupUseCase
import hyuuny.fooddelivery.optiongroups.domain.OptionGroup
import hyuuny.fooddelivery.options.domain.Option
import hyuuny.fooddelivery.options.infrastructure.OptionRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime

class ChangeOptionGroupUseCaseTest : BehaviorSpec({

    val repository = mockk<OptionRepository>()
    val useCase = OptionUseCase(repository)
    val optionGroupUseCase = mockk<OptionGroupUseCase>()

    Given("옵션의 옵션그룹을 변경할 때") {
        val now = LocalDateTime.now()
        val updateOptionGroupId = 70L
        val optionGroup = OptionGroup(
            id = updateOptionGroupId,
            menuId = 3L,
            name = "추천 메뉴",
            required = true,
            priority = 1,
            createdAt = now,
            updatedAt = now
        )

        val option = Option(
            id = 1,
            optionGroupId = optionGroup.id!!,
            name = "후라이드 + 양념",
            price = 1000,
            createdAt = now,
            updatedAt = now,
        )
        coEvery { optionGroupUseCase.getOptionGroup(any()) } returns optionGroup
        coEvery { repository.findById(any()) } returns option
        coEvery { repository.updateOptionGroupId(any()) } returns Unit

        `when`("존재하는 옵션과 옵션그룹이라면") {
            useCase.changeOptionGroup(option.id!!) { optionGroup }

            then("옵션의 옵션그룹을 변경할 수 있다.") {
                coVerify { repository.updateOptionGroupId(any()) }
            }
        }

        `when`("존재하지 않는 옵션이면") {
            coEvery { repository.findById(any()) } returns null

            then("옵션을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.changeOptionGroup(
                        id = 0,
                        getOptionGroup = { optionGroup }
                    )
                }
                ex.message shouldBe "0번 옵션을 찾을 수 없습니다."
            }
        }

        `when`("존재하지 않는 옵션그룹이면") {
            coEvery { optionGroupUseCase.getOptionGroup(any()) } throws NoSuchElementException("0번 옵션그룹을 찾을 수 없습니다.")

            then("옵션그룹을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.changeOptionGroup(
                        id = option.id!!,
                        getOptionGroup = { optionGroupUseCase.getOptionGroup(0) }
                    )
                }
                ex.message shouldBe "0번 옵션그룹을 찾을 수 없습니다."
            }
        }
    }

})
