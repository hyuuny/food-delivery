package hyuuny.fooddelivery.deliveries.application

import hyuuny.fooddelivery.common.constant.DeliveryStatus
import hyuuny.fooddelivery.deliveries.domain.Delivery
import hyuuny.fooddelivery.deliveries.infrastructure.DeliveryRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class GetDeliveryUseCaseTest : BehaviorSpec({

    val repository = mockk<DeliveryRepository>()
    val useCase = DeliveryUseCase(repository)

    Given("배달 내역을 조회할 때") {
        val orderId = 1L
        val riderId = 293L

        val now = LocalDateTime.now()
        val delivery = Delivery(
            id = 1L,
            orderId = orderId,
            riderId = riderId,
            status = DeliveryStatus.DELIVERED,
            createdAt = now,
        )
        coEvery { repository.findById(any()) } returns delivery

        When("존재하는 배달내역이면") {
            val result = useCase.getDelivery(delivery.id!!)

            Then("상세조회할 수 있다.") {
                result.id shouldBe delivery.id
                result.orderId shouldBe orderId
                result.riderId shouldBe riderId
                result.status shouldBe DeliveryStatus.DELIVERED
                result.pickupTime.shouldBeNull()
                result.deliveredTime.shouldBeNull()
                result.cancelTime.shouldBeNull()
                result.createdAt shouldBe now
            }

            When("존재하지 않는 배달내역이면") {
                coEvery { repository.findById(any()) } returns null

                Then("배달 내역을 찾을 수 없다는 메세지가 반환된다.") {
                    val ex = shouldThrow<NoSuchElementException> {
                        useCase.getDelivery(0)
                    }
                    ex.message shouldBe "0번 배달 내역을 찾을 수 없습니다."
                }
            }
        }
    }
})
