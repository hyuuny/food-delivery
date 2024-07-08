package hyuuny.fooddelivery.useraddresses.infrastructure

import hyuuny.fooddelivery.useraddresses.domain.UserAddress
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserAddressDao : CoroutineCrudRepository<UserAddress, Long> {

    suspend fun findAllByUserId(userId: Long): List<UserAddress>

}
