package hyuuny.fooddelivery.infrastructure.useraddress

import hyuuny.fooddelivery.domain.useraddress.UserAddress
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.applyAndAwait
import org.springframework.data.r2dbc.core.update
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Component

@Component
class UserAddressRepositoryImpl(
    private val dao: UserAddressDao,
    private val template: R2dbcEntityTemplate,
) : UserAddressRepository {

    override suspend fun insert(userAddress: UserAddress): UserAddress = dao.save(userAddress)

    override suspend fun findById(id: Long): UserAddress? = dao.findById(id)

    override suspend fun findAllByUserId(userId: Long): List<UserAddress> = dao.findAllByUserId(userId)

    override suspend fun update(userAddress: UserAddress) {
        template.update<UserAddress>()
            .matching(
                Query.query(
                    where("user_id").`is`(userAddress.userId)
                        .and("id").`is`(userAddress.id!!)
                )
            ).applyAndAwait(
                Update.update("name", userAddress.name)
                    .set("zipCode", userAddress.zipCode)
                    .set("address", userAddress.address)
                    .set("detailAddress", userAddress.detailAddress)
                    .set("messageToRider", userAddress.messageToRider)
                    .set("entrancePassword", userAddress.entrancePassword)
                    .set("routeGuidance", userAddress.routeGuidance)
                    .set("updatedAt", userAddress.updatedAt)
            )
    }

    override suspend fun updateSelectedAddresses(userAddress: List<UserAddress>) {
        userAddress.forEach {
            template.update<UserAddress>()
                .matching(
                    Query.query(
                        where("user_id").`is`(it.userId)
                            .and("id").`is`(it.id!!)
                    ),
                ).applyAndAwait(
                    Update.update("selected", it.selected)
                        .set("updatedAt", it.updatedAt)
                )
        }
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }
}
