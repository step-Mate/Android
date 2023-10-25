package jinproject.stepwalk.home.screen

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import jinproject.stepwalk.home.screen.state.Day
import jinproject.stepwalk.home.screen.state.HealthCareExtras
import jinproject.stepwalk.home.screen.state.HeartRate
import jinproject.stepwalk.home.screen.state.HeartRateFactory
import jinproject.stepwalk.home.screen.state.HeartRateTabFactory
import jinproject.stepwalk.home.screen.state.Step
import jinproject.stepwalk.home.screen.state.StepFactory
import jinproject.stepwalk.home.screen.state.StepTabFactory
import jinproject.stepwalk.home.screen.state.Time
import jinproject.stepwalk.home.screen.state.Week
import jinproject.stepwalk.home.utils.onKorea
import java.time.LocalDateTime

internal class HomeUiStatePreviewParameters : PreviewParameterProvider<HomeUiState> {
    private val day = HomeUiStatePreview(Day)
    private val week = HomeUiStatePreview(Week)

    override val values: Sequence<HomeUiState> = sequenceOf(
        HomeUiState(
            step = StepTabFactory(day.steps).create(
                time = day.time,
                goal = 3000
            ),
            heartRate = HeartRateTabFactory(day.heartRates).create(
                time = day.time,
                goal = 90
            ),
            user = User.getInitValues(),
            time = day.time
        ),
        HomeUiState(
            step = StepTabFactory(week.steps).create(
                time = week.time,
                goal = 3000
            ),
            heartRate = HeartRateTabFactory(week.heartRates).create(
                time = week.time,
                goal = 90
            ),
            user = User.getInitValues(),
            time = week.time
        ),
    )
}

private class HomeUiStatePreview(val time: Time) {
    private val now: LocalDateTime = LocalDateTime.now()
    private val startTime: LocalDateTime = now.withHour(0)
    private val endTime: LocalDateTime get() = startTime.plusHours(1L)
    val steps = kotlin.run {
        mutableListOf<Step>().apply {
            repeat(time.toNumberOfDays()) { idx ->
                add(
                    StepFactory.instance.create(
                        startTime = startTime.onKorea().plusHours(idx.toLong()).toEpochSecond(),
                        endTime = endTime.onKorea().plusHours(idx.toLong()).toEpochSecond(),
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
                        startTime = startTime.onKorea().plusHours(idx.toLong()).toEpochSecond(),
                        endTime = endTime.onKorea().plusHours(idx.toLong()).toEpochSecond(),
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