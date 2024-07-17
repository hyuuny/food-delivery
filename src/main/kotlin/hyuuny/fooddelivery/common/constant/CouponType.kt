package hyuuny.fooddelivery.common.constant

enum class CouponType(private val value: String) {
    STORE("매장"),
    CATEGORY("메뉴 카테고리");

    fun isStore(): Boolean = this == STORE
    fun isCategory(): Boolean = this == CATEGORY
}