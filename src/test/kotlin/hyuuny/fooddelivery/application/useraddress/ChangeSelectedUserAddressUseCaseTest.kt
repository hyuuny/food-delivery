package hyuuny.fooddelivery.application.useraddress

import hyuuny.fooddelivery.domain.useraddress.UserAddress
import hyuuny.fooddelivery.infrastructure.useraddress.UserAddressRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class ChangeSelectedUserAddressUseCaseTest : BehaviorSpec({

    val repository = mockk<UserAddressRepository>()
    val useCase = UserAddressUseCase(repository)

    Given("회원이 배달받을 주소를 선택하면") {
        val id = 1L
        val userId = 1L
        val targetId = 2L

        val now = LocalDateTime.now()
        val exisingUserAddresses = listOf(
            UserAddress(
                id = 1,
                userId = userId,
                name = "주소1",
                zipCode = "12345",
                address = "서울특별시 강남구",
                detailAddress = "124번지",
                messageToRider = "문 앞에 놓아주세요.",
                entrancePassword = null,
                routeGuidance = null,
                selected = true,
                createdAt = now.minusDays(1),
                updatedAt = now.minusDays(1),
            ),
            UserAddress(
                id = targetId,
                userId = userId,
                name = "주소2",
                zipCode = "12345",
                address = "서울특별시 강남구",
                detailAddress = "125번지",
                messageToRider = "문 앞에 놓아주세요.",
                entrancePassword = null,
                routeGuidance = null,
                selected = false,
                createdAt = now.minusDays(1),
                updatedAt = now.minusDays(1),
            ),
            UserAddress(
                id = 3,
                userId = userId,
                name = "주소3",
                zipCode = "12345",
                address = "서울특별시 강남구",
                detailAddress = "126번지",
                messageToRider = "문 앞에 놓아주세요.",
                entrancePassword = null,
                routeGuidance = null,
                selected = false,
                createdAt = now.minusDays(1),
                updatedAt = now.minusDays(1),
            ),
        )

        coEvery { repository.findById(any()) } returns exisingUserAddresses[1]
        coEvery { repository.findAllByUserId(any()) } returns exisingUserAddresses
        coEvery { repository.updateSelectedAddresses(any()) } returns Unit

        When("선택한 주소가 배달받을 주소로 변경되고, 나머지 주소는 배달받지 않는 주소로") {
            useCase.changeUserAddressSelectedToTrue(id, targetId)

            Then("변경된다.") {
                coEvery { repository.updateSelectedAddresses(any()) }
                coEvery {
                    repository.updateSelectedAddresses(match {
                        it.filterIndexed { idx, _ -> idx % 2 == 0 }.all { address -> !address.selected }
                    })
                }
            }
        }

        When("존재하지 않는 주소이면") {
            coEvery { repository.findById(any()) } returns null

            Then("회원의 주소를 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.changeUserAddressSelectedToTrue(userId, 0)
                }
                ex.message shouldBe "회원의 0번 주소를 찾을 수 없습니다."
            }
        }
    }

})
