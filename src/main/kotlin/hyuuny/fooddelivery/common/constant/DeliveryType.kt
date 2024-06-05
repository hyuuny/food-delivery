package hyuuny.fooddelivery.common.constant

enum class DeliveryType(val value: String) {
    SELF("자체배달"),
    OUTSOURCING("배달업체 이용"),
    TAKE_OUT("포장")
}