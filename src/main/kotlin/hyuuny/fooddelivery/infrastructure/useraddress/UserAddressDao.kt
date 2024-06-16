package hyuuny.fooddelivery.infrastructure.useraddress

import hyuuny.fooddelivery.domain.useraddress.UserAddress
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserAddressDao : CoroutineCrudRepository<UserAddress, Long> {

    suspend fun findAllByUserId(userId: Long): List<UserAddress>

}
