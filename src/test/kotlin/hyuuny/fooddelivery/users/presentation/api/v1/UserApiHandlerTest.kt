package hyuuny.fooddelivery.users.presentation.api.v1

import ChangeUserEmailRequest
import ChangeUserImageUrlRequest
import ChangeUserNameRequest
import ChangeUserNicknameRequest
import ChangeUserPhoneNumberRequest
import SignUpUserRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.BaseIntegrationTest
import hyuuny.fooddelivery.common.constant.UserType
import hyuuny.fooddelivery.users.application.UserUseCase
import hyuuny.fooddelivery.users.domain.User
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.time.LocalDateTime

class UserApiHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: UserUseCase

    @DisplayName("사용자는 회원가입을 할 수 있다.")
    @Test
    fun signUp() {
        val request = SignUpUserRequest(
            name = "김성현",
            nickname = "hyuunu",
            email = "shyune@knou.ac.kr",
            phoneNumber = "010-1234-5678",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
        )
        val user = generateUser(request)
        coEvery { useCase.signUp(request) } returns user

        webTestClient.post().uri("/api/v1/users/sign-up/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(user.id!!)
            .jsonPath("$.userType").isEqualTo(user.userType.name)
            .jsonPath("$.name").isEqualTo(user.name)
            .jsonPath("$.nickname").isEqualTo(user.nickname)
            .jsonPath("$.email").isEqualTo(user.email)
            .jsonPath("$.phoneNumber").isEqualTo(user.phoneNumber)
            .jsonPath("$.imageUrl").isEqualTo(user.imageUrl!!)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    @DisplayName("라이더는 회원가입을 할 수 있다.")
    @Test
    fun signUpRider() {
        val request = SignUpUserRequest(
            name = "라이더",
            nickname = "rider123",
            email = "rider123@knou.ac.kr",
            phoneNumber = "010-8392-1280",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/rider-start.jpeg",
        )
        val rider = generateUser(request, UserType.RIDER)
        coEvery { useCase.signUpRider(request) } returns rider

        webTestClient.post().uri("/api/v1/users/sign-up/riders")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(rider.id!!)
            .jsonPath("$.userType").isEqualTo(rider.userType.name)
            .jsonPath("$.name").isEqualTo(rider.name)
            .jsonPath("$.nickname").isEqualTo(rider.nickname)
            .jsonPath("$.email").isEqualTo(rider.email)
            .jsonPath("$.phoneNumber").isEqualTo(rider.phoneNumber)
            .jsonPath("$.imageUrl").isEqualTo(rider.imageUrl!!)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    @DisplayName("회원은 자신의 정보를 조회할 수 있다.")
    @Test
    fun getUser() {
        val request = SignUpUserRequest(
            name = "김성현",
            nickname = "hyuunu",
            email = "shyune@knou.ac.kr",
            phoneNumber = "010-1234-5678",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
        )
        val user = generateUser(request)
        coEvery { useCase.getUser(any()) } returns user

        webTestClient.get().uri("/api/v1/users/${user.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(user.id!!)
            .jsonPath("$.userType").isEqualTo(user.userType.name)
            .jsonPath("$.name").isEqualTo(user.name)
            .jsonPath("$.nickname").isEqualTo(user.nickname)
            .jsonPath("$.email").isEqualTo(user.email)
            .jsonPath("$.phoneNumber").isEqualTo(user.phoneNumber)
            .jsonPath("$.imageUrl").isEqualTo(user.imageUrl!!)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    @DisplayName("회원은 자신의 이름을 변경할 수 있다.")
    @Test
    fun changeName() {
        val id = 1
        val request = ChangeUserNameRequest(name = "김철수")
        coEvery { useCase.changeName(any(), any()) } returns Unit

        webTestClient.patch().uri("/api/v1/users/$id/change-name")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("회원은 자신의 닉네임을 변경할 수 있다.")
    @Test
    fun changeNickname() {
        val id = 1
        val request = ChangeUserNicknameRequest(nickname = "만두")
        coEvery { useCase.changeNickname(any(), any()) } returns Unit

        webTestClient.patch().uri("/api/v1/users/$id/change-nickname")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("회원은 자신의 이메일을 변경할 수 있다.")
    @Test
    fun changeEmail() {
        val id = 1
        val request = ChangeUserEmailRequest(email = "hyuuny@naver.com")
        coEvery { useCase.changeEmail(any(), any()) } returns Unit

        webTestClient.patch().uri("/api/v1/users/$id/change-email")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("회원은 자신의 휴대폰 번호를 변경할 수 있다.")
    @Test
    fun changePhoneNumber() {
        val id = 1
        val request = ChangeUserPhoneNumberRequest(phoneNumber = "010-1234-5678")
        coEvery { useCase.changePhoneNumber(any(), any()) } returns Unit

        webTestClient.patch().uri("/api/v1/users/$id/change-phone-number")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("회원은 자신의 이미지를 변경할 수 있다.")
    @Test
    fun changeImageUrl() {
        val id = 1
        val request =
            ChangeUserImageUrlRequest(imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/star.jpeg")
        coEvery { useCase.changeImageUrl(any(), any()) } returns Unit

        webTestClient.patch().uri("/api/v1/users/$id/change-image-url")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("회원은 서비스를 탈퇴할 수 있다.")
    @Test
    fun deleteUser() {
        val id = 1
        coEvery { useCase.deleteUser(any()) } returns Unit

        webTestClient.delete().uri("/api/v1/users/$id")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    private fun generateUser(request: SignUpUserRequest, userType: UserType = UserType.CUSTOMER): User {
        val now = LocalDateTime.now()
        return User(
            id = 1,
            userType = userType,
            name = request.name,
            nickname = request.nickname,
            email = request.email,
            phoneNumber = request.phoneNumber,
            imageUrl = request.imageUrl,
            createdAt = now,
            updatedAt = now,
        )
    }
}
