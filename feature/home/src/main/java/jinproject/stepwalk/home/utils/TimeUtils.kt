package jinproject.stepwalk.home.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun LocalDateTime.onKorea() = this.atZone(ZoneOffset.of("Asia/Seoul"))

fun Instant.onKorea() = this.atZone(ZoneId.of("Asia/Seoul"))