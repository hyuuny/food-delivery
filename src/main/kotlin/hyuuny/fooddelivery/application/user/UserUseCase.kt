package hyuuny.fooddelivery.application.user

import ChangeUserNameCommand
import ChangeUserNameRequest
import ChangeUserNicknameCommand
import ChangeUserNicknameRequest
import SignUpUserCommand
import SignUpUserRequest
import hyuuny.fooddelivery.domain.user.User
import hyuuny.fooddelivery.infrastructure.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class UserUseCase(
    private val repository: UserRepository,
) {

    @Transactional
    suspend fun signUp(request: SignUpUserRequest): User {
        if (repository.existsByEmail(request.email)) throw IllegalArgumentException("중복된 이메일입니다. email: ${request.email} ")
        UserVerifier.verify(request)

        val now = LocalDateTime.now()
        val user = User.handle(
            SignUpUserCommand(
                name = request.name,
                nickname = request.nickname,
                email = request.email,
                phoneNumber = request.phoneNumber,
                imageUrl = request.imageUrl,
                createdAt = now,
                updatedAt = now,
            )
        )
        return repository.insert(user)
    }

    suspend fun getUser(id: Long): User = findUserByIdOrThrow(id)

    @Transactional
    suspend fun changeName(id: Long, request: ChangeUserNameRequest) {
        UserVerifier.verifyName(request.name)
        val user = findUserByIdOrThrow(id)

        val now = LocalDateTime.now()
        user.handle(
            ChangeUserNameCommand(
                name = request.name,
                updatedAt = now,
            )
        )
        repository.updateName(user)
    }

    @Transactional
    suspend fun changeNickname(id: Long, request: ChangeUserNicknameRequest) {
        UserVerifier.verifyNickname(request.nickname)
        val user = findUserByIdOrThrow(id)

        val now = LocalDateTime.now()
        user.handle(
            ChangeUserNicknameCommand(
                nickname = request.nickname,
                updatedAt = now,
            )
        )
        repository.updateNickname(user)
    }

    private suspend fun findUserByIdOrThrow(id: Long): User =
        repository.findById(id) ?: throw NoSuchElementException("${id}번 회원을 찾을 수 없습니다.")

}
