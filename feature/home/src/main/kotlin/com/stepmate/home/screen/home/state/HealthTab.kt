package com.stepmate.home.screen.home.state

import androidx.compose.runtime.Stable

internal abstract class HealthTabFactory<T : HealthCare>(
    open var healthCareList: List<T>,
) {
    protected val total get() = healthCareList.sumOf { it.figure }

    abstract fun create(time: Time, goal: Int): HealthTab

    abstract fun getDefaultValues(time: Time): HealthTab
}

@Stable
internal data class HealthTab(
    val header: HealthPage,
    val graph: List<Long>,
    val menu: List<MenuItem>,
) {
    companion object {
        fun getDefaultGraphItems(dayCount: Int): ArrayList<Long> = ArrayList<Long>(dayCount).apply {
            repeat(dayCount) { index ->
                add(index, 0L)
            }
        }
    }
}

internal data class HealthPage(
    val total: Long,
    val goal: Int,
    val title: String,
)