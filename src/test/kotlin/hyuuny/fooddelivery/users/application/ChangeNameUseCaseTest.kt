package hyuuny.fooddelivery.users.application

import ChangeUserNameRequest
import hyuuny.fooddelivery.users.domain.User
import hyuuny.fooddelivery.users.infrastructure.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class ChangeNameUseCaseTest : BehaviorSpec({

    val repository = mockk<UserRepository>()
    val useCase = UserUseCase(repository)
    val verifier = mockk<UserVerifier>()

    Given("회원이 자신의 이름을 변경 할 때") {
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
        val request = ChangeUserNameRequest(name = "김철수")
        coEvery { repository.findById(any()) } returns user
        coEvery { repository.updateName(any()) } returns Unit

        `when`("유효한 값이면") {
            useCase.changeName(id, request)

            then("정상적으로 이름을 변경할 수 있다.") {
                coEvery { repository.updateName(any()) }
            }
        }

        When("이름이 2자 미만이면") {
            val invalidRequest = ChangeUserNameRequest(name = "김")
            coEvery { verifier.verifyName(invalidRequest.name) } throws IllegalArgumentException("이름은 최소 2자 이상이여야 합니다. name: ${invalidRequest.name}")

            Then("이름을 변경 할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.changeName(id, invalidRequest)
                }
                ex.message shouldBe "이름은 최소 2자 이상이여야 합니다. name: ${invalidRequest.name}"
            }
        }
    }

})
