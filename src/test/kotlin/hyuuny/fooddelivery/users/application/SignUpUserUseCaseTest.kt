package hyuuny.fooddelivery.users.application

import SignUpUserRequest
import hyuuny.fooddelivery.common.constant.UserType
import hyuuny.fooddelivery.users.domain.User
import hyuuny.fooddelivery.users.infrastructure.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class SignUpUserUseCaseTest : BehaviorSpec({

    val repository = mockk<UserRepository>()
    val useCase = UserUseCase(repository)
    val verifier = mockk<UserVerifier>()

    Given("회원가입시에") {
        val request = SignUpUserRequest(
            name = "김성현",
            nickname = "hyuuny",
            email = "shyune@knou.ac.kr",
            phoneNumber = "010-1234-1234",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
        )

        val now = LocalDateTime.now()
        val user = User(
            id = 1L,
            name = request.name,
            nickname = request.nickname,
            email = request.email,
            phoneNumber = request.phoneNumber,
            imageUrl = request.imageUrl,
            createdAt = now,
            updatedAt = now,
        )
        coEvery { repository.existsByEmail(request.email) } returns false
        coEvery { repository.insert(any()) } returns user

        When("고객이 회원가입하면") {
            val result = useCase.signUp(request)

            Then("유저 타입이 CUSTOMER로 회원가입에 성공한다.") {
                result.id.shouldNotBeNull()
                result.userType shouldBe UserType.CUSTOMER
                result.name shouldBe request.name
                result.nickname shouldBe request.nickname
                result.email shouldBe request.email
                result.phoneNumber shouldBe request.phoneNumber
                result.imageUrl shouldBe request.imageUrl
                result.createdAt.shouldNotBeNull()
                result.updatedAt shouldBe result.createdAt
            }
        }

        When("라이더가 회원가입하면") {
            coEvery { repository.insert(any()) } returns User(
                id = 1L,
                userType = UserType.RIDER,
                name = request.name,
                nickname = request.nickname,
                email = request.email,
                phoneNumber = request.phoneNumber,
                imageUrl = request.imageUrl,
                createdAt = now,
                updatedAt = now,
            )
            val result = useCase.signUpRider(request)

            Then("유저 타입이 RIDER로 회원가입에 성공한다.") {
                result.id.shouldNotBeNull()
                result.userType shouldBe UserType.RIDER
                result.name shouldBe request.name
                result.nickname shouldBe request.nickname
                result.email shouldBe request.email
                result.phoneNumber shouldBe request.phoneNumber
                result.imageUrl shouldBe request.imageUrl
                result.createdAt.shouldNotBeNull()
                result.updatedAt shouldBe result.createdAt
            }
        }

        When("중복된 이메일이면") {
            coEvery { repository.existsByEmail(any()) } returns true

            Then("회원가입을 할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.signUp(request)
                }
                ex.message shouldBe "중복된 이메일입니다. email: ${request.email} "
            }
        }

        When("이름이 2자 미만이면") {
            val invalidRequest = SignUpUserRequest(
                name = "김", // 이름이 2자 미만인 경우
                nickname = "nickname",
                email = "shyune@knou.ac.kr",
                phoneNumber = "010-1234-5678",
                imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
            )

            coEvery { repository.existsByEmail(any()) } returns false
            coEvery { verifier.verify(invalidRequest) } throws IllegalArgumentException("이름은 최소 2자 이상이여야 합니다. name: ${invalidRequest.name}")

            Then("회원가입을 할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.signUp(invalidRequest)
                }
                ex.message shouldBe "이름은 최소 2자 이상이여야 합니다. name: ${invalidRequest.name}"
            }
        }

        When("닉네임이 2자 미만이면") {
            val invalidRequest = SignUpUserRequest(
                name = "김성현",
                nickname = "h", // 닉네임이 2자 미만인 경우
                email = "shyune@knou.ac.kr",
                phoneNumber = "010-1234-5678",
                imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
            )

            coEvery { repository.existsByEmail(any()) } returns false
            coEvery { verifier.verify(invalidRequest) } throws IllegalArgumentException("닉네임은 2자 이상 10자 이하여야 합니다. nickname: ${invalidRequest.nickname}")

            Then("회원가입을 할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.signUp(invalidRequest)
                }
                ex.message shouldBe "닉네임은 2자 이상 10자 이하여야 합니다. nickname: ${invalidRequest.nickname}"
            }
        }

        When("닉네임이 10자 초과이면") {
            val invalidRequest = SignUpUserRequest(
                name = "김성현",
                nickname = "hyuunyhyuuny", // 닉네임이 10자 초과인 경우
                email = "shyune@knou.ac.kr",
                phoneNumber = "010-1234-5678",
                imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
            )
            coEvery { repository.existsByEmail(any()) } returns false
            coEvery { verifier.verify(invalidRequest) } throws IllegalArgumentException("닉네임은 2자 이상 10자 이하여야 합니다. nickname: ${invalidRequest.nickname}")

            Then("회원가입을 할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.signUp(invalidRequest)
                }
                ex.message shouldBe "닉네임은 2자 이상 10자 이하여야 합니다. nickname: ${invalidRequest.nickname}"
            }
        }

        When("올바르지 않은 이메일 형식이면") {
            val invalidRequest = SignUpUserRequest(
                name = "김성현",
                nickname = "hyuuny",
                email = "shyune@knou", // 이메일 형식이 잘못된 경우
                phoneNumber = "010-1234-5678",
                imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
            )
            coEvery { repository.existsByEmail(any()) } returns false
            coEvery { verifier.verify(invalidRequest) } throws IllegalArgumentException("올바른 이메일 형식이 아닙니다. email: ${invalidRequest.email}")

            Then("회원가입을 할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.signUp(invalidRequest)
                }
                ex.message shouldBe "올바른 이메일 형식이 아닙니다. email: ${invalidRequest.email}"
            }
        }

        When("올바르지 않은 휴대폰 번호 형식이면") {
            val invalidRequest = SignUpUserRequest(
                name = "김성현",
                nickname = "hyuuny",
                email = "shyune@knou.ac.kr",
                phoneNumber = "010-1234-567", // 휴대폰 번호 형식이 잘못된 경우
                imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
            )
            coEvery { repository.existsByEmail(any()) } returns false
            coEvery { verifier.verify(invalidRequest) } throws IllegalArgumentException("올바른 휴대폰 번호 형식이 아닙니다. phoneNumber: ${invalidRequest.phoneNumber}")

            Then("회원가입을 할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.signUp(invalidRequest)
                }
                ex.message shouldBe "올바른 휴대폰 번호 형식이 아닙니다. phoneNumber: ${invalidRequest.phoneNumber}"
            }
        }
    }
})
