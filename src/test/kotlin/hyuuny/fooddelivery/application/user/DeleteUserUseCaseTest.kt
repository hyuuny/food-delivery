package hyuuny.fooddelivery.application.user

import hyuuny.fooddelivery.infrastructure.user.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

class DeleteUserUseCaseTest : BehaviorSpec({

    val repository = mockk<UserRepository>()
    val useCase = UserUseCase(repository)

    Given("회원이 탈퇴를 요청했을 때") {
        val id = 1L
        coEvery { repository.existsById(any()) } returns true
        coEvery { repository.delete(any()) } returns Unit

        When("존재하는 회원이라면") {
            useCase.deleteUser(id)

            Then("정상적으로 탈퇴할 수 있다.") {
                coEvery { repository.delete(any()) }
            }
        }

        When("존재하지 않는 회원이라면") {
            coEvery { repository.existsById(any()) } returns false

            Then("회원을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.deleteUser(0)
                }
                ex.message shouldBe "0번 회원을 찾을 수 없습니다."
            }
        }
    }
})
