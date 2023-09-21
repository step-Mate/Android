package jinproject.stepwalk.home.utils

import jinproject.stepwalk.home.state.weekToString
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import java.util.Locale

fun LocalDateTime.onKorea(): ZonedDateTime = this.atZone(ZoneOffset.of("+9"))

fun Instant.onKorea(): ZonedDateTime = this.atZone(ZoneOffset.of("+9"))

fun Instant.toLocalDateTime(): LocalDateTime = this.onKorea().toLocalDateTime()

fun Int.toDayOfWeekString(): String = kotlin.run {
    val dayOfWeek = LocalDate
    .now()
    .with(ChronoField.DAY_OF_WEEK, this.toLong())
    .dayOfWeek

    dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())
}