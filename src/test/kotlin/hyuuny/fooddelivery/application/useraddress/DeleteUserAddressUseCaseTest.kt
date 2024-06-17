package hyuuny.fooddelivery.application.useraddress

import hyuuny.fooddelivery.domain.useraddress.UserAddress
import hyuuny.fooddelivery.infrastructure.useraddress.UserAddressRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class DeleteUserAddressUseCaseTest : BehaviorSpec({

    val repository = mockk<UserAddressRepository>()
    val useCase = UserAddressUseCase(repository)

    Given("회원이 주소를 삭제할 때") {
        val id = 1L
        val userId = 1L
        val now = LocalDateTime.now()
        val userAddress = UserAddress(
            id = id,
            userId = userId,
            name = "우리집",
            zipCode = "12345",
            address = "서울특별시 강남구",
            detailAddress = "123번지",
            messageToRider = "문 앞에 놓아주세요.",
            entrancePassword = "1234",
            routeGuidance = "오른쪽으로 도세요.",
            selected = true,
            createdAt = now.minusDays(1),
            updatedAt = now.minusDays(1),
        )

        coEvery { repository.findById(any()) } returns userAddress
        coEvery { repository.delete(any()) } returns Unit

        When("존재하는 주소라면") {
            useCase.deleteUserAddress(id)

            Then("정상적으로 삭제할 수 있다.") {
                coEvery { repository.delete(any()) }
            }
        }

        When("존재하지 않는 주소이면") {
            coEvery { repository.findById(any()) } returns null

            Then("회원의 주소를 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.deleteUserAddress(0)
                }
                ex.message shouldBe "회원의 0번 주소를 찾을 수 없습니다."
            }
        }
    }

})
