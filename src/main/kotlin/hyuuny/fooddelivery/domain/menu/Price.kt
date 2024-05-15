package hyuuny.fooddelivery.domain.menu

@JvmInline
value class Price(val value: Long) {

    init {
        validate()
    }

    private fun validate() {
        if (value < 0) throw IllegalStateException("금액은 0이상이여야 합니다.")
    }

}