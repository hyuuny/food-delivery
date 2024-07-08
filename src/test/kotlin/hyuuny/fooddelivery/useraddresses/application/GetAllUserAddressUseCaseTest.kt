package hyuuny.fooddelivery.useraddresses.application

import hyuuny.fooddelivery.useraddresses.domain.UserAddress
import hyuuny.fooddelivery.useraddresses.infrastructure.UserAddressRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class GetAllUserAddressUseCaseTest : BehaviorSpec({

    val repository = mockk<UserAddressRepository>()
    val useCase = UserAddressUseCase(repository)

    Given("회원은 자신이 등록한") {
        val userId = 1L
        val now = LocalDateTime.now()
        val userAddresses = listOf(
            UserAddress(
                id = 4,
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
            UserAddress(
                id = 2,
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
                id = 1,
                userId = userId,
                name = "주소1",
                zipCode = "12345",
                address = "서울특별시 강남구",
                detailAddress = "124번지",
                messageToRider = "문 앞에 놓아주세요.",
                entrancePassword = null,
                routeGuidance = null,
                selected = false,
                createdAt = now.minusDays(1),
                updatedAt = now.minusDays(1),
            ),
        )

        coEvery { repository.findAllByUserId(any()) } returns userAddresses

        When("주소 목록을") {
            val result = useCase.getAllUserAddress(userId)

            Then("조회할 수 있다.") {
                result.size shouldBe userAddresses.size
                result.forEachIndexed { idx, userAddress ->
                    userAddress.id.shouldNotBeNull()
                    userAddress.userId shouldBe userId
                    userAddress.name shouldBe userAddresses[idx].name
                    userAddress.zipCode shouldBe userAddresses[idx].zipCode
                    userAddress.address shouldBe userAddresses[idx].address
                    userAddress.detailAddress shouldBe userAddresses[idx].detailAddress
                    userAddress.messageToRider shouldBe userAddresses[idx].messageToRider
                    userAddress.entrancePassword shouldBe userAddresses[idx].entrancePassword
                    userAddress.routeGuidance shouldBe userAddresses[idx].routeGuidance
                    userAddress.selected shouldBe userAddresses[idx].selected
                    userAddress.createdAt.shouldNotBeNull()
                    userAddress.updatedAt shouldBe userAddresses[idx].createdAt
                }
            }
        }

        When("주소 목록이 없으면") {
            coEvery { repository.findAllByUserId(any()) } returns emptyList()
            val result = useCase.getAllUserAddress(userId)

            Then("빈 화면을 보게된다.") {
                result.shouldBeEmpty()
            }
        }
    }

})
