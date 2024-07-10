package hyuuny.fooddelivery.common.constant

enum class DeliveryStatus(val value: String) {
    ACCEPTED("수락"),
    DELIVERING("배달 중"),
    DELIVERED("배달 완료"),
    CANCELED("취소");

    companion object {
        val CANCELABLE_DELIVERY_STATUS = setOf(ACCEPTED)
    }
}
