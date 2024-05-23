package hyuuny.fooddelivery.application.menuoption

import CreateMenuOptionRequest
import hyuuny.fooddelivery.domain.menuoption.MenuOption
import hyuuny.fooddelivery.infrastructure.menuoption.MenuOptionRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class CreateMenuOptionUseCaseTest : BehaviorSpec({

    val repository = mockk<MenuOptionRepository>()
    val useCase = MenuOptionUseCase(repository)

    Given("메뉴 옵션을 등록하면서") {
        val request = CreateMenuOptionRequest(
            menuGroupId = 1L,
            name = "후라이드 + 양념",
            price = 1000
        )

        val now = LocalDateTime.now()
        val expectedMenuOption = MenuOption(
            id = 1,
            menuGroupId = 1L,
            name = "후라이드 + 양념",
            price = 1000,
            createdAt = now,
            updatedAt = now,
        )
        coEvery { repository.insert(any()) } returns expectedMenuOption

        `when`("옵션명을 입력하면") {
            val result = useCase.createMenuOption(request)

            then("메뉴 옵션을 등록할 수 있다.") {
                result.id.shouldNotBeNull()
                result.menuGroupId shouldBe request.menuGroupId
                result.name shouldBe request.name
                result.price shouldBe request.price
                result.createdAt.shouldNotBeNull()
                result.updatedAt shouldBe result.createdAt
            }
        }

        `when`("옵션명을 입력하지 않고 공백으로 제출하면") {
            then("메뉴 옵션을 등록할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createMenuOption(
                        CreateMenuOptionRequest(
                            menuGroupId = 1L,
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