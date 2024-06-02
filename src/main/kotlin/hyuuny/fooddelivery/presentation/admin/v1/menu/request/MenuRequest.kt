import hyuuny.fooddelivery.domain.menu.MenuStatus

data class CreateMenuRequest(
    val menuGroupId: Long,
    val name: String,
    val price: Long,
    val status: MenuStatus,
    val popularity: Boolean,
    val imageUrl: String?,
    val description: String?,
)

data class UpdateMenuRequest(
    val name: String,
    val price: Long,
    val popularity: Boolean,
    val imageUrl: String?,
    val description: String?,
)

data class ChangeMenuStatusRequest(
    val status: MenuStatus,
)

data class MenuSearchCondition(
    val name: String? = null,
    val status: MenuStatus? = null,
    val popularity: Boolean? = null,
)