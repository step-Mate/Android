package com.stepmate.home.screen.home

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.stepmate.home.screen.home.state.Day
import com.stepmate.home.screen.home.state.HealthCareExtras
import com.stepmate.home.screen.home.state.HeartRate
import com.stepmate.home.screen.home.state.HeartRateFactory
import com.stepmate.home.screen.home.state.HeartRateTabFactory
import com.stepmate.home.screen.home.state.Step
import com.stepmate.home.screen.home.state.StepFactory
import com.stepmate.home.screen.home.state.StepTabFactory
import com.stepmate.home.screen.home.state.Time
import com.stepmate.home.screen.home.state.User
import com.stepmate.home.screen.home.state.Week
import java.time.ZonedDateTime

internal class HomeUiStatePreviewParameters : PreviewParameterProvider<HomeUiState> {
    private val day = HomeUiStatePreview(Day)
    private val week = HomeUiStatePreview(Week)

    override val values: Sequence<HomeUiState> = sequenceOf(
        HomeUiState(
            step = StepTabFactory(day.steps, User.getInitValues()).create(
                time = day.time,
                goal = 50000
            ),
            heartRate = HeartRateTabFactory(day.heartRates).create(
                time = day.time,
                goal = 90
            ),
        ),
        HomeUiState(
            step = StepTabFactory(week.steps, User.getInitValues()).create(
                time = week.time,
                goal = 20000
            ),
            heartRate = HeartRateTabFactory(week.heartRates).create(
                time = week.time,
                goal = 90
            ),
        ),
    )
}

internal class HomeUiStatePreview(val time: Time) {
    private val now: ZonedDateTime = ZonedDateTime.now()
    private val startTime: ZonedDateTime = now.withHour(0)
    private val endTime: ZonedDateTime get() = startTime.plusHours(1L)
    val steps = kotlin.run {
        mutableListOf<Step>().apply {
            repeat(time.toNumberOfDays()) { idx ->
                add(
                    StepFactory.instance.create(
                        startTime = startTime.plusHours(idx.toLong()),
                        endTime = endTime.plusHours(idx.toLong()),
                        figure = 1000 + idx.toLong() * 50
                    )
                )
            }
        }
    }

    val heartRates = kotlin.run {
        mutableListOf<HeartRate>().apply {
            repeat(time.toNumberOfDays()) { idx ->
                add(
                    HeartRateFactory.instance.create(
                        startTime = startTime.plusHours(idx.toLong()),
                        endTime = endTime.plusHours(idx.toLong()),
                        extras = HealthCareExtras().apply {
                            set(HealthCareExtras.KEY_HEART_RATE_MAX, 100 + (idx).toLong())
                            set(HealthCareExtras.KEY_HEART_RATE_AVG, 75 + (idx).toLong())
                            set(HealthCareExtras.KEY_HEART_RATE_MIN, 50 + (idx).toLong())
                        }
                    )
                )
            }
        }
    }
}