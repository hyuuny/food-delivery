package hyuuny.fooddelivery.application.useraddress

import hyuuny.fooddelivery.domain.useraddress.UserAddress
import hyuuny.fooddelivery.infrastructure.useraddress.UserAddressRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class GetUserAddressUseCaseTest : BehaviorSpec({

    val repository = mockk<UserAddressRepository>()
    val useCase = UserAddressUseCase(repository)

    Given("회원이 자신의 주소를 상세조회 할 때") {
        val id = 1L
        val userId = 1L
        val now = LocalDateTime.now()
        val userAddress = UserAddress(
            id = id,
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
        )

        coEvery { repository.findById(any()) } returns userAddress

        When("존재하는 주소이면") {
            val result = useCase.getUserAddress(id)

            Then("상세조회할 수 있다.") {
                userAddress.id.shouldNotBeNull()
                userAddress.userId shouldBe id
                userAddress.name shouldBe result.name
                userAddress.zipCode shouldBe result.zipCode
                userAddress.address shouldBe result.address
                userAddress.detailAddress shouldBe result.detailAddress
                userAddress.messageToRider shouldBe result.messageToRider
                userAddress.entrancePassword shouldBe result.entrancePassword
                userAddress.routeGuidance shouldBe result.routeGuidance
                userAddress.selected shouldBe result.selected
                userAddress.createdAt.shouldNotBeNull()
                userAddress.updatedAt shouldBe result.createdAt
            }
        }

        When("존재하지 않는 주소이면") {
            coEvery { repository.findById(any()) } returns null

            Then("회원의 주소를 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.getUserAddress(0)
                }
                ex.message shouldBe "회원의 0번 주소를 찾을 수 없습니다."
            }
        }
    }

})
