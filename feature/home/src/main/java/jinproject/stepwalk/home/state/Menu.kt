package jinproject.stepwalk.home.state

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import java.util.SortedMap

internal interface HealthMenu {
    @Stable
    val details: MutableMap<String, MenuDetail>

    @Stable
    val graphItems: SortedMap<Int, Long>
}

@Stable
internal data class MenuDetail(
    val value: Float,
    @DrawableRes val img: Int,
    val intro: String
)