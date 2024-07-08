package hyuuny.fooddelivery.menus.application

import hyuuny.fooddelivery.common.constant.MenuStatus
import hyuuny.fooddelivery.menus.domain.Menu
import hyuuny.fooddelivery.menus.infrastructure.MenuRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

internal class GetMenuUseCaseTest : BehaviorSpec({

    val repository = mockk<MenuRepository>()
    val useCase = MenuUseCase(repository)

    given("메뉴를 상세조회 할 때") {
        val expectedMenuId = 1L
        val now = LocalDateTime.now()
        val expectedMenu = Menu(
            id = expectedMenuId,
            menuGroupId = 1L,
            name = "싸이버거",
            price = 6000,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "cyburger-image-url",
            description = "[베스트]닭다리살",
            createdAt = now,
            updatedAt = now
        )
        coEvery { repository.findById(any()) } returns expectedMenu

        `when`("존재하는 아이디이면") {
            val result = useCase.getMenu(expectedMenuId)

            then("메뉴를 상세조회 할 수 있다.") {
                result.id shouldBe expectedMenuId
                result.menuGroupId shouldBe expectedMenu.menuGroupId
                result.name shouldBe expectedMenu.name
                result.price shouldBe expectedMenu.price
                result.status shouldBe expectedMenu.status
                result.popularity shouldBe expectedMenu.popularity
                result.imageUrl shouldBe expectedMenu.imageUrl
                result.description shouldBe expectedMenu.description
                result.createdAt shouldBe expectedMenu.createdAt
                result.updatedAt shouldBe expectedMenu.updatedAt
            }
        }

        `when`("존재하지 않는 아이디이면") {
            coEvery { repository.findById(any()) } returns null

            then("존재하지 않는 메뉴라는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.getMenu(0)
                }
                ex.message shouldBe "0번 메뉴를 찾을 수 없습니다."
            }
        }
    }
})
