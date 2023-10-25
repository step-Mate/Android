package jinproject.stepwalk.home.screen.state

import androidx.compose.runtime.Stable

/**
 * 헬스케어 정보
 * @property startTime 시작시간
 * @property endTime 끝시간
 * @property figure 특정 헬스케어 정보의 값
 */
@Stable
abstract class HealthCare(
    open val startTime: Long,
    open val endTime: Long,
    open val figure: Long
)

interface HealthCareFactory<T : HealthCare> {
    fun create(
        startTime: Long,
        endTime: Long,
        figure: Long,
    ): T

    fun create(
        startTime: Long,
        endTime: Long,
        extras: HealthCareExtras,
    ): T
}

class HealthCareExtras(
    extras: MutableMap<String, Long> = emptyMap<String, Long>().toMutableMap()
) {
    val map: MutableMap<String, Long> = extras

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