package jinproject.stepwalk.home.screen.home

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import jinproject.stepwalk.home.screen.home.state.Day
import jinproject.stepwalk.home.screen.home.state.HealthCareExtras
import jinproject.stepwalk.home.screen.home.state.HeartRate
import jinproject.stepwalk.home.screen.home.state.HeartRateFactory
import jinproject.stepwalk.home.screen.home.state.HeartRateTabFactory
import jinproject.stepwalk.home.screen.home.state.Step
import jinproject.stepwalk.home.screen.home.state.StepFactory
import jinproject.stepwalk.home.screen.home.state.StepTabFactory
import jinproject.stepwalk.home.screen.home.state.Time
import jinproject.stepwalk.home.screen.home.state.User
import jinproject.stepwalk.home.screen.home.state.Week
import java.time.ZonedDateTime

internal class HomeUiStatePreviewParameters : PreviewParameterProvider<HomeUiState> {
    private val day = HomeUiStatePreview(Day)
    private val week = HomeUiStatePreview(Week)

    override val values: Sequence<HomeUiState> = sequenceOf(
        HomeUiState(
            step = StepTabFactory(day.steps).create(
                time = day.time,
                goal = 50000
            ),
            heartRate = HeartRateTabFactory(day.heartRates).create(
                time = day.time,
                goal = 90
            ),
            user = User.getInitValues(),
        ),
        HomeUiState(
            step = StepTabFactory(week.steps).create(
                time = week.time,
                goal = 20000
            ),
            heartRate = HeartRateTabFactory(week.heartRates).create(
                time = week.time,
                goal = 90
            ),
            user = User.getInitValues(),
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