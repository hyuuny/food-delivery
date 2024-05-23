data class CreateMenuOptionRequest(
    val menuGroupId: Long,
    val name: String,
    val price: Long
)

data class MenuOptionSearchCondition(
    val menuGroupId: Long? = null,
    val name: String? = null,
)