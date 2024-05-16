package hyuuny.fooddelivery.application.menu

import CreateMenuRequest
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.domain.menu.MenuStatus
import hyuuny.fooddelivery.domain.menu.Price
import hyuuny.fooddelivery.infrastructure.menu.MenuRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

internal class CreateMenuUseCaseTest : BehaviorSpec({

    val repository = mockk<MenuRepository>()
    val useCase = MenuUseCase(repository)

    given("메뉴를 등록하면서") {
        val request = CreateMenuRequest(
            name = "싸이버거",
            price = 6000,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "cyburger-image-url",
            description = "[베스트]닭다리살"
        )

        val now = LocalDateTime.now()
        val expectedMenu = Menu(
            id = 1,
            name = "싸이버거",
            price = Price(6000),
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "cyburger-image-url",
            description = "[베스트]닭다리살",
            createdAt = now,
            updatedAt = now
        )
        coEvery { repository.insert(any()) } returns expectedMenu

        `when`("금액을 0이상으로 입력하면") {
            val result = useCase.createMenu(request)

            then("메뉴를 등록할 수 있다.") {
                result.id.shouldNotBeNull()
                result.name shouldBe result.name
                result.price shouldBe result.price
                result.status shouldBe result.status
                result.popularity shouldBe result.popularity
                result.imageUrl shouldBe result.imageUrl
                result.description shouldBe result.description
                result.createdAt.shouldNotBeNull()
                result.updatedAt.shouldNotBeNull()
            }
        }

        `when`("금액을 0원 이하로 입력하면") {
            then("메뉴를 등록할 수 없다.") {
                shouldThrow<IllegalArgumentException> {
                    useCase.createMenu(
                        CreateMenuRequest(
                            name = "싸이버거",
                            price = 0,
                            status = MenuStatus.ON_SALE,
                            popularity = true,
                            imageUrl = "cyburger-image-url",
                            description = "[베스트]닭다리살"
                        )
                    )
                }
            }
        }
    }
})