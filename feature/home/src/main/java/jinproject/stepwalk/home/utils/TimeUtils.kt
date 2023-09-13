package jinproject.stepwalk.home.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

fun LocalDateTime.onKorea(): ZonedDateTime = this.atZone(ZoneOffset.of("+9"))

fun Instant.onKorea(): ZonedDateTime = this.atZone(ZoneOffset.of("+9"))

fun Instant.toLocalDateTime(): LocalDateTime = this.onKorea().toLocalDateTime()