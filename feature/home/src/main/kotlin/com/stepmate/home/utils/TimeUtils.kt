package com.stepmate.home.utils

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import java.util.Locale

internal fun LocalDateTime.onKorea(): ZonedDateTime = this.atZone(ZoneOffset.of("+9"))

internal fun Instant.onKorea(): ZonedDateTime = this.atZone(ZoneOffset.of("+9"))

/**
 * DayOfWeek 값으로 일~토 로 변환하는 함수
 * @param this : 1~7(월~일)
 * @exception IllegalArgumentException 값이 1~7 허용치를 벗어난 경우
 */
internal fun Int.toDayOfWeekString(): String = kotlin.run {
    if (this < 1 || this > 7)
        throw IllegalArgumentException("잘못된 입력 $this : 정수(1~7)만 입력 가능")

    val dayOfWeek = LocalDate
        .now()
        .with(ChronoField.DAY_OF_WEEK, this.toLong())
        .dayOfWeek

    dayOfWeek.displayOnKorea()
}

internal fun DayOfWeek.displayOnKorea() = this.getDisplayName(
    java.time.format.TextStyle.SHORT,
    Locale.getDefault()
)