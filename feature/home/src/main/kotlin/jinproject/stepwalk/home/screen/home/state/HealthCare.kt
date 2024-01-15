package jinproject.stepwalk.home.screen.home.state

import androidx.compose.runtime.Stable
import java.time.ZonedDateTime

/**
 * 헬스케어 정보
 * @property startTime 시작시간
 * @property endTime 끝시간
 * @property figure 특정 헬스케어 정보의 값
 */
@Stable
internal abstract class HealthCare(
    open val startTime: ZonedDateTime,
    open val endTime: ZonedDateTime,
    open val figure: Long,
)

internal interface HealthCareFactory<T : HealthCare> {
    fun create(
        startTime: ZonedDateTime,
        endTime: ZonedDateTime,
        figure: Long,
    ): T

    fun create(
        startTime: ZonedDateTime,
        endTime: ZonedDateTime,
        extras: HealthCareExtras,
    ): T
}

internal class HealthCareExtras(
    extras: MutableMap<String, Long> = emptyMap<String, Long>().toMutableMap(),
) {
    private val map: MutableMap<String, Long> = extras

    operator fun get(key: String): Long? {
        return map[key]
    }

    fun set(key: String, value: Long) {
        map[key] = value
    }

    companion object {
        const val KEY_STEP = "KEY_STEP"
        const val KEY_HEART_RATE_MAX = "KEY_HEART_RATE_MAX"
        const val KEY_HEART_RATE_MIN = "KEY_HEART_RATE_MIN"
        const val KEY_HEART_RATE_AVG = "KEY_HEART_RATE_AVG"
    }
}