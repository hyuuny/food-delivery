package hyuuny.fooddelivery.application.useraddress

import hyuuny.fooddelivery.domain.useraddress.UserAddress
import hyuuny.fooddelivery.infrastructure.useraddress.UserAddressRepository
import hyuuny.fooddelivery.presentation.api.v1.useraddress.request.UpdateUserAddressRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class UpdateUserAddressUseCaseTest : BehaviorSpec({

    val repository = mockk<UserAddressRepository>()
    val useCase = UserAddressUseCase(repository)

    Given("회원이 주소를 수정할 때") {
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

        val request = UpdateUserAddressRequest(
            name = "친구집",
            zipCode = "34918",
            address = "경기도 부천시 안곡로 181-6",
            detailAddress = "502호",
            messageToRider = "조심히 오세요",
            entrancePassword = null,
            routeGuidance = null,
        )
        coEvery { repository.findById(any()) } returns userAddress
        coEvery { repository.update(any()) } returns Unit

        When("입력한 주소 정보로") {
            useCase.updateUserAddress(id, request)

            Then("수정할 수 있다.") {
                coEvery { repository.update(any()) }
            }
        }

        When("존재하지 않는 주소이면") {
            coEvery { repository.findById(any()) } returns null

            Then("회원의 주소를 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.updateUserAddress(0, request)
                }
                ex.message shouldBe "회원의 0번 주소를 찾을 수 없습니다."
            }
        }
    }

})
