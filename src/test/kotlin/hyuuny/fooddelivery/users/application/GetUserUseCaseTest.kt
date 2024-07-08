package hyuuny.fooddelivery.users.application

import hyuuny.fooddelivery.users.domain.User
import hyuuny.fooddelivery.users.infrastructure.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class GetUserUseCaseTest : BehaviorSpec({

    val repository = mockk<UserRepository>()
    val useCase = UserUseCase(repository)

    Given("회원이 자신의 정보를 상세조회 할 때") {
        val id = 1L
        val now = LocalDateTime.now()
        val expectedUser = User(
            id = 1L,
            name = "김성현",
            nickname = "hyuuny",
            email = "shyune@knou.ac.kr",
            phoneNumber = "010-1234-1234",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
            createdAt = now,
            updatedAt = now,
        )
        coEvery { repository.findById(any()) } returns expectedUser

        `when`("존재하는 회원이면") {
            val result = useCase.getUser(id)

            then("정보를 상세조회 할 수 있다.") {
                result.id shouldBe id
                result.name shouldBe expectedUser.name
                result.nickname shouldBe expectedUser.nickname
                result.email shouldBe expectedUser.email
                result.phoneNumber shouldBe expectedUser.phoneNumber
                result.imageUrl shouldBe expectedUser.imageUrl
                result.createdAt.shouldNotBeNull()
                result.updatedAt shouldBe result.createdAt
            }
        }

        `when`("존재하지 않는 회원이면") {
            coEvery { repository.findById(any()) } returns null

            then("회원을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.getUser(0)
                }
                ex.message shouldBe "0번 회원을 찾을 수 없습니다."
            }
        }
    }

})
