package hyuuny.fooddelivery.common.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun convertToLocalDate(localDate: String): LocalDate = localDate.let {
    LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
}
