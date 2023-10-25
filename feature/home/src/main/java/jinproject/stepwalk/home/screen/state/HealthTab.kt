package jinproject.stepwalk.home.screen.state

internal abstract class HealthTabFactory<T : HealthCare>(
    open var healthCareList: List<T>,
) {
    protected val total get() = healthCareList.sumOf { it.figure }

    abstract fun create(time: Time, goal: Int): HealthTab
    abstract fun getMenuList(): List<MenuItem>
}

internal data class HealthTab(
    val header: HealthPage,
    val graph: List<Long>,
    val menu: List<MenuItem>,
) {
    companion object {
        fun getInitValues(dayCount: Int) = HealthTab(
            header = HealthPage.getInitValues(),
            graph = getDefaultList(dayCount),
            menu = emptyList()
        )

        private fun getDefaultList(dayCount: Int): List<Long> = ArrayList<Long>(dayCount).apply {
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
) {
    companion object {
        fun getInitValues() = HealthPage(
            total = 0L,
            goal = 0,
            title = ""
        )
    }
}