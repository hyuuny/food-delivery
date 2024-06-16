package hyuuny.fooddelivery.application.useraddress

import hyuuny.fooddelivery.domain.useraddress.UserAddress
import hyuuny.fooddelivery.domain.useraddress.UserAddress.Companion.MAX_USER_ADDRESS_COUNT
import hyuuny.fooddelivery.infrastructure.useraddress.UserAddressRepository
import hyuuny.fooddelivery.presentation.api.v1.useraddress.request.CreateUserAddressRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class CreateUserAddressUseCaseTest : BehaviorSpec({

    val repository = mockk<UserAddressRepository>()
    val useCase = UserAddressUseCase(repository)

    Given("회원이 주소를 새로 등록하면서") {
        val userId = 1L
        val now = LocalDateTime.now()
        val request = CreateUserAddressRequest(
            name = "우리집",
            zipCode = "12345",
            address = "서울특별시 강남구",
            detailAddress = "123번지",
            messageToRider = "문 앞에 놓아주세요.",
            entrancePassword = "1234",
            routeGuidance = "오른쪽으로 도세요.",
        )

        val userAddress = UserAddress(
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
        )

        coEvery { repository.findAllByUserId(any()) } returns emptyList()
        coEvery { repository.updateSelectedAddresses(any()) } returns Unit
        coEvery { repository.insert(any()) } returns userAddress

        When("등록하는 주소가 선택된 주소로") {
            val result = useCase.createUserAddress(userId, request)

            Then("자동 설정된다.") {
                result.id.shouldNotBeNull()
                result.userId shouldBe userId
                result.name shouldBe request.name
                result.zipCode shouldBe request.zipCode
                result.address shouldBe request.address
                result.detailAddress shouldBe request.detailAddress
                result.messageToRider shouldBe request.messageToRider
                result.entrancePassword shouldBe request.entrancePassword
                result.routeGuidance shouldBe request.routeGuidance
                result.selected shouldBe true
                result.createdAt.shouldNotBeNull()
                result.updatedAt shouldBe result.createdAt
            }
        }

        When("기존에 등록된 주소가 존재하면") {
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
            coEvery { repository.findAllByUserId(any()) } returns exisingUserAddresses
            val result = useCase.createUserAddress(userId, request)

            Then("기존 주소는 모두 선택되지 않은 주소로 변경된다.") {
                result.id.shouldNotBeNull()
                result.userId shouldBe userId
                result.name shouldBe request.name
                result.zipCode shouldBe request.zipCode
                result.address shouldBe request.address
                result.detailAddress shouldBe request.detailAddress
                result.messageToRider shouldBe request.messageToRider
                result.entrancePassword shouldBe request.entrancePassword
                result.routeGuidance shouldBe request.routeGuidance
                result.selected shouldBe true
                result.createdAt.shouldNotBeNull()
                result.updatedAt shouldBe result.createdAt

                coEvery {
                    repository.updateSelectedAddresses(match {
                        it.all { address -> !address.selected }
                    })
                }
            }
        }

        When("이미 등록된 주소지가 10개라면") {
            val userAddresses = List(MAX_USER_ADDRESS_COUNT) { userAddress }
            coEvery { repository.findAllByUserId(any()) } returns userAddresses

            Then("주소는 최대 10개까지만 등록할 수 있다는 메세지가 반환된다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createUserAddress(userId, request)
                }
                ex.message shouldBe "주소는 최대 10개까지만 등록할 수 있습니다."
            }
        }
    }

})
