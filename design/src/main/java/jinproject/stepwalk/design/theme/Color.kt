package jinproject.stepwalk.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Immutable
@JvmInline
value class MiscellaneousToolColor private constructor(val color: Color){
    companion object {
        @Stable
        private val primary = MiscellaneousToolColor(Color(0xFF7FD182))
        @Stable
        private val deep_primary = MiscellaneousToolColor(Color(0xFF315532))
        @Stable
        private val lightBlack = MiscellaneousToolColor(Color(0xFF1B1B1E))
        @Stable
        private val black = MiscellaneousToolColor(Color(0xFF151515))
        @Stable
        private val white = MiscellaneousToolColor(Color(0XFFFFFFFF))
        @Stable
        private val lightGray = MiscellaneousToolColor(Color(0xFFDADDE1))
        @Stable
        private val mediumGray = MiscellaneousToolColor(Color(0xFF8A8A8A))
        @Stable
        private val deepGray = MiscellaneousToolColor(Color(0xFF575757))
        @Stable
        val red = MiscellaneousToolColor(Color(0xFFE0302D))
        @Stable
        private val deepRed = MiscellaneousToolColor(Color(0xFF800006))
        @Stable
        val blue = MiscellaneousToolColor(Color(0xFF007AFF))
        @Stable
        val yellow_200 = MiscellaneousToolColor(Color(0xFFFFF59D))
        @Stable
        val yellow_300 = MiscellaneousToolColor(Color(0xFFFFF176))
        @Stable
        val yellow_400 = MiscellaneousToolColor(Color(0xFFFFEE58))
        @Stable
        val yellow_500 = MiscellaneousToolColor(Color(0xFFFFEB3B))
        @Stable
        val yellow_600 = MiscellaneousToolColor(Color(0xFFFDD835))
        @Stable
        val yellow_700 = MiscellaneousToolColor(Color(0xFFFBC02D))

        @Stable
        val orange_200 = MiscellaneousToolColor(Color(0xFFFFCC80))
        @Stable
        val orange_300 = MiscellaneousToolColor(Color(0xFFFFB74D))
        @Stable
        val orange_400 = MiscellaneousToolColor(Color(0xFFFFA726))
        @Stable
        val orange_500 = MiscellaneousToolColor(Color(0xFFFF9800))
        @Stable
        val orange_600 = MiscellaneousToolColor(Color(0xFFFB8C00))

        val light_primary = primary
        val light_onPrimary = white
        @Stable
        val light_secondary = lightGray
        val light_onSecondary = lightBlack
        val light_error = red
        @Stable
        val light_onError = MiscellaneousToolColor(Color(0xFF410001))
        val light_background = white
        val light_onBackground = lightBlack
        val light_surface = white
        val light_onSurface = lightBlack
        val light_onSurfaceVariant = lightBlack
        val light_scrim = mediumGray
        val light_outline = mediumGray

        val dark_primary = deep_primary
        val dark_onPrimary = lightGray
        @Stable
        val dark_secondary = deepGray
        val dark_onSecondary = lightGray
        @Stable
        val dark_error = MiscellaneousToolColor(Color(0xFFFFB4A9))
        val dark_onError = deepRed
        val dark_background = black // 컨테이너 색상
        val dark_onBackground = lightGray
        val dark_surface = lightBlack // 상단바 색상
        val dark_onSurface = lightGray
        val dark_onSurfaceVariant = deepGray
        val dark_scrim = lightGray
        val dark_outline = lightGray
    }
}