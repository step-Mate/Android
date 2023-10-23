package jinproject.stepwalk.home.state

import androidx.compose.runtime.Stable
import jinproject.stepwalk.domain.model.METs

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
}

class HealthCareExtras() {
    val map: MutableMap<String, Value<*>> = mutableMapOf()

    interface Value<T>

    operator fun <T> get(key: String): T? {
        @Suppress("UNCHECKED_CAST")
        return map[key] as T?
    }

    companion object {
        val VALUE_METs = object : Value<METs> {}
        val VALUE_HEART_RATE = object : Value<Long> {}
    }
}