import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun generatePaymentId(): String {
    val uuid = UUID.randomUUID()
    return "PAY_${uuid.toString().replace("-", "").substring(0, 10)}"
}

fun generateOrderNumber(now: LocalDateTime = LocalDateTime.now()): String {
    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
    val formattedDateTime = formatter.format(now)
    return "O_$formattedDateTime"
}
