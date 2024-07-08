package hyuuny.fooddelivery.useraddresses.presentation.api.v1

import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.BaseIntegrationTest
import hyuuny.fooddelivery.useraddresses.application.UserAddressUseCase
import hyuuny.fooddelivery.useraddresses.domain.UserAddress
import hyuuny.fooddelivery.useraddresses.presentation.api.v1.request.CreateUserAddressRequest
import hyuuny.fooddelivery.useraddresses.presentation.api.v1.request.UpdateUserAddressRequest
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.time.LocalDateTime

class UserAddressApiHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: UserAddressUseCase

    @DisplayName("회원은 배달 주소지를 설정할 수 있다.")
    @Test
    fun createUserAddress() {
        val userId = 1L
        val request = CreateUserAddressRequest(
            name = "우리집",
            zipCode = "12345",
            address = "서울시 강남구 역삼동",
            detailAddress = "위워크 빌딩 19층",
            messageToRider = "1층 로비에 보관 부탁드립니다",
            entrancePassword = null,
            routeGuidance = "역삼역 2번 출구에서 도보 5분",
        )
        val userAddress = generateUserAddress(request, selected = true)
        coEvery { useCase.createUserAddress(any(), any()) } returns userAddress

        webTestClient.post().uri("/api/v1/users/$userId/addresses")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(userAddress.id!!)
            .jsonPath("$.name").isEqualTo(userAddress.name ?: userAddress.address)
            .jsonPath("$.zipCode").isEqualTo(userAddress.zipCode)
            .jsonPath("$.address").isEqualTo(userAddress.address)
            .jsonPath("$.detailAddress").isEqualTo(userAddress.detailAddress)
            .jsonPath("$.messageToRider").isEqualTo(userAddress.messageToRider!!)
            .jsonPath("$.entrancePassword").doesNotExist()
            .jsonPath("$.routeGuidance").isEqualTo(userAddress.routeGuidance!!)
            .jsonPath("$.selected").isEqualTo(userAddress.selected)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    @DisplayName("회원은 자신이 등록한 주소 목록을 볼 수 있다.")
    @Test
    fun getAllUserAddresses() {
        val userId = 1L
        val now = LocalDateTime.now()
        val userAddresses = listOf(
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
        )
        coEvery { useCase.getAllUserAddress(any()) } returns userAddresses
        userAddresses.sortedWith(compareByDescending<UserAddress> { it.selected }.thenByDescending { it.id })

        webTestClient.get().uri("/api/v1/users/$userId/addresses")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.length()").isEqualTo(userAddresses.size)
            .jsonPath("$[0].id").isEqualTo(userAddresses[3].id!!)
            .jsonPath("$[0].userId").isEqualTo(userAddresses[3].userId)
            .jsonPath("$[0].name").isEqualTo(userAddresses[3].name ?: userAddresses[3].address)
            .jsonPath("$[0].zipCode").isEqualTo(userAddresses[3].zipCode)
            .jsonPath("$[0].address").isEqualTo(userAddresses[3].address)
            .jsonPath("$[0].detailAddress").isEqualTo(userAddresses[3].detailAddress)
            .jsonPath("$[0].messageToRider").isEqualTo(userAddresses[3].messageToRider!!)
            .jsonPath("$[0].entrancePassword").isEqualTo(userAddresses[3].entrancePassword!!)
            .jsonPath("$[0].routeGuidance").isEqualTo(userAddresses[3].routeGuidance!!)
            .jsonPath("$[0].selected").isEqualTo(true)
            .jsonPath("$[0].createdAt").exists()
            .jsonPath("$[0].updatedAt").exists()

            .jsonPath("$[1].id").isEqualTo(userAddresses[0].id!!)
            .jsonPath("$[1].userId").isEqualTo(userAddresses[0].userId)
            .jsonPath("$[1].name").isEqualTo(userAddresses[0].name ?: userAddresses[0].address)
            .jsonPath("$[1].zipCode").isEqualTo(userAddresses[0].zipCode)
            .jsonPath("$[1].address").isEqualTo(userAddresses[0].address)
            .jsonPath("$[1].detailAddress").isEqualTo(userAddresses[0].detailAddress)
            .jsonPath("$[1].messageToRider").isEqualTo(userAddresses[0].messageToRider!!)
            .jsonPath("$[1].entrancePassword").doesNotExist()
            .jsonPath("$[1].routeGuidance").doesNotExist()
            .jsonPath("$[1].selected").isEqualTo(false)
            .jsonPath("$[1].createdAt").exists()
            .jsonPath("$[1].updatedAt").exists()
    }

    @DisplayName("회원은 자신이 등록한 주소를 상세조회 할 수 있다.")
    @Test
    fun getUserAddress() {
        val request = CreateUserAddressRequest(
            name = "우리집",
            zipCode = "12345",
            address = "서울시 강남구 역삼동",
            detailAddress = "위워크 빌딩 19층",
            messageToRider = "1층 로비에 보관 부탁드립니다",
            entrancePassword = null,
            routeGuidance = "역삼역 2번 출구에서 도보 5분",
        )
        val userAddress = generateUserAddress(request, selected = true)
        coEvery { useCase.getUserAddress(any()) } returns userAddress

        webTestClient.get().uri("/api/v1/users/${userAddress.userId}/addresses/${userAddress.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(userAddress.id!!)
            .jsonPath("$.name").isEqualTo(userAddress.name ?: userAddress.address)
            .jsonPath("$.zipCode").isEqualTo(userAddress.zipCode)
            .jsonPath("$.address").isEqualTo(userAddress.address)
            .jsonPath("$.detailAddress").isEqualTo(userAddress.detailAddress)
            .jsonPath("$.messageToRider").isEqualTo(userAddress.messageToRider!!)
            .jsonPath("$.entrancePassword").doesNotExist()
            .jsonPath("$.routeGuidance").isEqualTo(userAddress.routeGuidance!!)
            .jsonPath("$.selected").isEqualTo(userAddress.selected)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    @DisplayName("회원은 자신이 등록한 주소 정보를 수정할 수 있다.")
    @Test
    fun updateUserAddress() {
        val id = 1L
        val userId = 1L
        val request = UpdateUserAddressRequest(
            name = "친구집",
            zipCode = "34918",
            address = "경기도 부천시 안곡로 181-6",
            detailAddress = "502호",
            messageToRider = "조심히 오세요",
            entrancePassword = null,
            routeGuidance = null,
        )
        coEvery { useCase.updateUserAddress(any(), any()) } returns Unit

        webTestClient.put().uri("/api/v1/users/${userId}/addresses/${id}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("회원은 배달받을 주소를 자신의 주소목록에서 선택해서 변경할 수 있다.")
    @Test
    fun changeUserAddressSelectedToTrue() {
        val userId = 1L
        val id = 2L
        coEvery { useCase.changeUserAddressSelectedToTrue(any(), any()) } returns Unit

        webTestClient.patch().uri("/api/v1/users/$userId/addresses/$id/change-selected")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("회원은 자신이 등록한 주소를 삭제할 수 있다.")
    @Test
    fun deleteUserAddress() {
        val userId = 1L
        val id = 2L
        coEvery { useCase.deleteUserAddress(any()) } returns Unit

        webTestClient.delete().uri("/api/v1/users/$userId/addresses/$id")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    private fun generateUserAddress(request: CreateUserAddressRequest, selected: Boolean): UserAddress {
        val now = LocalDateTime.now()
        return UserAddress(
            id = 1L,
            userId = 1L,
            name = request.name,
            zipCode = request.zipCode,
            address = request.address,
            detailAddress = request.detailAddress,
            messageToRider = request.messageToRider,
            entrancePassword = request.entrancePassword,
            routeGuidance = request.routeGuidance,
            selected = selected,
            createdAt = now,
            updatedAt = now,
        )
    }
}
