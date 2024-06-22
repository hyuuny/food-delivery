package hyuuny.fooddelivery.common.constant

enum class OrderStatus(val value: String) {
    CREATED("주문 생성"),
    PENDING("결제 대기 중"),
    PAYMENT_COMPLETED("결제 완료"),
    PAYMENT_FAILED("결제 실패"),
    CONFIRMED("주문 확인"),
    PROCESSING("처리중"),
    OUT_FOR_DELIVERY("배달 중"),
    DELIVERY_COMPLETED("배달 완료"),
    CANCELLED_BY_USER("사용자 취소"),
    CANCELLED_BY_STORE("가게 취소"),
    REFUNDED("환불"),
    FAILED("실패");

    companion object {
        val CANCELABLE_ORDER_STATUS = setOf(CREATED, PENDING, PAYMENT_COMPLETED)
        val REFUNDABLE_ORDER_STATUS = setOf(DELIVERY_COMPLETED)
    }

}
