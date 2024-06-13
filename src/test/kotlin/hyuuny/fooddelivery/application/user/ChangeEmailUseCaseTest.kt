package hyuuny.fooddelivery.application.user

import ChangeEmailRequest
import hyuuny.fooddelivery.domain.user.User
import hyuuny.fooddelivery.infrastructure.user.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class ChangeEmailUseCaseTest : BehaviorSpec({

    val repository = mockk<UserRepository>()
    val useCase = UserUseCase(repository)
    val verifier = mockk<UserVerifier>()

    Given("회원이 자신의 이메일을 변경 할 때") {
        val id = 1L
        val now = LocalDateTime.now()
        val user = User(
            id = 1L,
            name = "김성현",
            nickname = "hyuuny",
            email = "shyune@knou.ac.kr",
            phoneNumber = "010-1234-1234",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
            createdAt = now,
            updatedAt = now,
        )
        val request = ChangeEmailRequest(email = "hyuuny@naver.com")
        coEvery { repository.findById(any()) } returns user
        coEvery { repository.updateEmail(any()) } returns Unit

        `when`("유효한 값이면") {
            useCase.changeEmail(id, request)

            then("정상적으로 이메일을 변경할 수 있다.") {
                coEvery { repository.updateEmail(any()) }
            }
        }

        When("올바르지 않은 이메일 형식이면") {
            val invalidRequest = ChangeEmailRequest(email = "hyuuny@naver123")
            coEvery { verifier.verifyEmail(invalidRequest.email) } throws IllegalArgumentException("올바른 이메일 형식이 아닙니다. email: ${invalidRequest.email}")

            Then("닉네임을 변경 할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.changeEmail(id, invalidRequest)
                }
                ex.message shouldBe "올바른 이메일 형식이 아닙니다. email: ${invalidRequest.email}"
            }
        }
    }

})
