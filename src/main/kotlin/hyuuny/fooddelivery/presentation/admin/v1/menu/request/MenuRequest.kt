import hyuuny.fooddelivery.domain.menu.MenuStatus

data class CreateMenuRequest(
    val name: String,
    val price: Long,
    val status: MenuStatus,
    val popularity: Boolean,
    val imageUrl: String?,
    val description: String?,
)