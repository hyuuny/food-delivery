package hyuuny.fooddelivery.presentation.api.v1.user

import ChangeEmailRequest
import ChangeUserNameRequest
import ChangeUserNicknameRequest
import SignUpUserRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.user.UserUseCase
import hyuuny.fooddelivery.domain.user.User
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
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

        webTestClient.post().uri("/api/v1/users/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(user.id!!)
            .jsonPath("$.name").isEqualTo(user.name)
            .jsonPath("$.nickname").isEqualTo(user.nickname)
            .jsonPath("$.email").isEqualTo(user.email)
            .jsonPath("$.phoneNumber").isEqualTo(user.phoneNumber)
            .jsonPath("$.imageUrl").isEqualTo(user.imageUrl!!)
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
        val request = ChangeEmailRequest(email = "hyuuny@naver.com")
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

    private fun generateUser(request: SignUpUserRequest): User {
        val now = LocalDateTime.now()
        return User(
            id = 1,
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
