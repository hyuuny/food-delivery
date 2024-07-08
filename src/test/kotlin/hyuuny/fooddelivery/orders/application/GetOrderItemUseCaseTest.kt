package hyuuny.fooddelivery.orders.application

import hyuuny.fooddelivery.orders.domain.OrderItem
import hyuuny.fooddelivery.orders.infrastructure.OrderItemRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class GetOrderItemUseCaseTest : BehaviorSpec({

    val repository = mockk<OrderItemRepository>()
    val useCase = OrderItemUseCase(repository)

    Given("주문내역에 포함된") {
        val orderId = 1L

        val now = LocalDateTime.now()
        val orderItems = listOf(
            OrderItem(1L, orderId, 1L, "피자", 15000, 1, now),
            OrderItem(2L, orderId, 2L, "불고기 버거", 5000, 1, now),
        )
        coEvery { useCase.getAllByOrderId(any()) } returns orderItems

        `when`("주문 항목들을") {
            val result = useCase.getAllByOrderId(orderId)

            then("조회할 수 있다.") {
                result.forEachIndexed { idx, orderItem ->
                    orderItem.id shouldBe orderItems[idx].id
                    orderItem.orderId shouldBe orderItems[idx].orderId
                    orderItem.menuId shouldBe orderItems[idx].menuId
                    orderItem.menuName shouldBe orderItems[idx].menuName
                    orderItem.menuPrice shouldBe orderItems[idx].menuPrice
                    orderItem.quantity shouldBe orderItems[idx].quantity
                }
            }
        }
    }

})
