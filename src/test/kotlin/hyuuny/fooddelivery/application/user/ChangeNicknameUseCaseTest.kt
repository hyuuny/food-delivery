package hyuuny.fooddelivery.application.user

import ChangeUserNicknameRequest
import hyuuny.fooddelivery.domain.user.User
import hyuuny.fooddelivery.infrastructure.user.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class ChangeNicknameUseCaseTest : BehaviorSpec({

    val repository = mockk<UserRepository>()
    val useCase = UserUseCase(repository)
    val verifier = mockk<UserVerifier>()

    Given("회원이 자신의 닉네임을 변경 할 때") {
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
        val request = ChangeUserNicknameRequest(nickname = "만두")
        coEvery { repository.findById(any()) } returns user
        coEvery { repository.updateNickname(any()) } returns Unit

        `when`("유효한 값이면") {
            useCase.changeNickname(id, request)

            then("정상적으로 닉네임을 변경할 수 있다.") {
                coEvery { repository.updateNickname(any()) }
            }
        }

        When("닉네임이 2자 미만이면") {
            val invalidRequest = ChangeUserNicknameRequest(nickname = "만")
            coEvery { verifier.verifyNickname(invalidRequest.nickname) } throws IllegalArgumentException("닉네임은 2자 이상 10자 이하여야 합니다. nickname: ${invalidRequest.nickname}")

            Then("닉네임을 변경 할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.changeNickname(id, invalidRequest)
                }
                ex.message shouldBe "닉네임은 2자 이상 10자 이하여야 합니다. nickname: ${invalidRequest.nickname}"
            }
        }

        When("닉네임이 10자 초과라면") {
            val invalidRequest = ChangeUserNicknameRequest(nickname = "일이삼사오육칠팔구십일")
            coEvery { verifier.verifyNickname(invalidRequest.nickname) } throws IllegalArgumentException("닉네임은 2자 이상 10자 이하여야 합니다. nickname: ${invalidRequest.nickname}")

            Then("닉네임을 변경 할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.changeNickname(id, invalidRequest)
                }
                ex.message shouldBe "닉네임은 2자 이상 10자 이하여야 합니다. nickname: ${invalidRequest.nickname}"
            }
        }
    }

})
