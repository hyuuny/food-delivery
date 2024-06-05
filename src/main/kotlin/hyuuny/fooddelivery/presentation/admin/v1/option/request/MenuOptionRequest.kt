data class CreateOptionRequest(
    val optionGroupId: Long,
    val name: String,
    val price: Long
)

data class UpdateOptionRequest(
    val name: String,
    val price: Long,
)

data class AdminOptionSearchCondition(
    val optionGroupId: Long? = null,
    val name: String? = null,
)
