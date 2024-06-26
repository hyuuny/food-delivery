import java.math.BigDecimal
import java.math.RoundingMode

fun truncateToSingleDecimalPlace(value: Double): Double {
    return BigDecimal(value).setScale(1, RoundingMode.DOWN).toDouble()
}