package hyuuny.fooddelivery.optiongroups.application

import UpdateOptionGroupRequest
import hyuuny.fooddelivery.optiongroups.domain.OptionGroup
import hyuuny.fooddelivery.optiongroups.infrastructure.OptionGroupRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime

internal class UpdateOptionGroupUseCaseTest : BehaviorSpec({

    val repository = mockk<OptionGroupRepository>()
    val useCase = OptionGroupUseCase(repository)

    given("옵션그룹을 수정 할 때") {
        val menuId = 5L
        val optionGroupId = 1L
        val now = LocalDateTime.now()
        val expectedOptionGroup = OptionGroup(
            id = optionGroupId,
            menuId = menuId,
            name = "반반치킨 선택",
            required = true,
            priority = 1,
            createdAt = now,
            updatedAt = now
        )
        val request = UpdateOptionGroupRequest(
            name = "사장님 추천 메뉴!",
            menuId = menuId,
            required = false,
        )
        coEvery { repository.findById(any()) } returns expectedOptionGroup
        coEvery { repository.update(any()) } returns Unit

        `when`("이름을 2글자 이상으로 입력하면") {
            useCase.updateOptionGroup(optionGroupId, request)
            then("옵션그룹을 수정 할 수 있다.") {
                coVerify { repository.update(any()) }
            }
        }

        `when`("이름을 2글자 미만으로 입력하면") {
            coEvery { repository.findById(any()) } returns expectedOptionGroup

            then("옵션그룹을 수정 할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.updateOptionGroup(
                        optionGroupId,
                        UpdateOptionGroupRequest(
                            name = "굳",
                            menuId = menuId,
                            required = false,
                        )
                    )
                }
                ex.message shouldBe "이름은 2자 이상이어야 합니다."
            }
        }
    }
})
