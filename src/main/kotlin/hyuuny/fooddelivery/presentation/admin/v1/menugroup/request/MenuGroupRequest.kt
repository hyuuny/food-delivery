data class CreateMenuGroupRequest(
    val menuId: Long,
    val name: String,
    val required: Boolean,
)

data class UpdateMenuGroupRequest(
    val menuId: Long,
    val name: String,
    val required: Boolean,
)

data class MenuGroupSearchCondition(
    val menuId: Long? = null,
    val name: String? = null,
)

