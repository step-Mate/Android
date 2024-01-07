package jinproject.stepwalk.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Immutable
@JvmInline
value class StepWalkColor private constructor(val color: Color){
    companion object {
        @Stable
        private val primary = StepWalkColor(Color(0xFF7FD182))
        @Stable
        private val deep_primary = StepWalkColor(Color(0xFF315532))
        @Stable
        private val lightBlack = StepWalkColor(Color(0xFF1B1B1E))
        @Stable
        private val black = StepWalkColor(Color(0xFF151515))
        @Stable
        private val white = StepWalkColor(Color(0XFFFFFFFF))
        @Stable
        private val lightGrey = StepWalkColor(Color(0xFFDADDE1))
        @Stable
        private val mediumGrey = StepWalkColor(Color(0xFF8A8A8A))
        @Stable
        private val deepGrey = StepWalkColor(Color(0xFF575757))
        @Stable
        private val grey_100 = StepWalkColor(Color(0xFFF5F5F5))
        @Stable
        private val grey_200 = StepWalkColor(Color(0xFFEEEEEE))
        @Stable
        private val grey_300 = StepWalkColor(Color(0xFFE0E0E0))
        @Stable
        private val grey_400 = StepWalkColor(Color(0xFFBDBDBD))
        @Stable
        private val grey_500 = StepWalkColor(Color(0xFF9E9E9E))
        @Stable
        private val grey_600 = StepWalkColor(Color(0xFF757575))
        @Stable
        private val grey_700 = StepWalkColor(Color(0xFF616161))
        @Stable
        private val grey_800 = StepWalkColor(Color(0xFF424242))
        @Stable
        private val grey_900 = StepWalkColor(Color(0xFF212121))
        @Stable
        val red = StepWalkColor(Color(0xFFE0302D))
        @Stable
        private val deepRed = StepWalkColor(Color(0xFF800006))
        @Stable
        val blue_200 = StepWalkColor(Color(0xFF90CAF9))
        @Stable
        val blue_300 = StepWalkColor(Color(0xFF64B5F6))
        @Stable
        val blue_400 = StepWalkColor(Color(0xFF42A5F5))
        @Stable
        val blue_500 = StepWalkColor(Color(0xFF2196F3))
        @Stable
        val blue_600 = StepWalkColor(Color(0xFF1E88E5))
        @Stable
        val blue_700 = StepWalkColor(Color(0xFF1976D2))
        @Stable
        val blue_800 = StepWalkColor(Color(0xFF1565C0))
        @Stable
        val blue_900 = StepWalkColor(Color(0xFF0D47A1))
        @Stable
        val yellow_200 = StepWalkColor(Color(0xFFFFF59D))
        @Stable
        val yellow_300 = StepWalkColor(Color(0xFFFFF176))
        @Stable
        val yellow_400 = StepWalkColor(Color(0xFFFFEE58))
        @Stable
        val yellow_500 = StepWalkColor(Color(0xFFFFEB3B))
        @Stable
        val yellow_600 = StepWalkColor(Color(0xFFFDD835))
        @Stable
        val yellow_700 = StepWalkColor(Color(0xFFFBC02D))

        @Stable
        val orange_200 = StepWalkColor(Color(0xFFFFCC80))
        @Stable
        val orange_300 = StepWalkColor(Color(0xFFFFB74D))
        @Stable
        val orange_400 = StepWalkColor(Color(0xFFFFA726))
        @Stable
        val orange_500 = StepWalkColor(Color(0xFFFF9800))
        @Stable
        val orange_600 = StepWalkColor(Color(0xFFFB8C00))

        @Stable
        val kakao_yellow = StepWalkColor(Color(0xFFFEE500))
        @Stable
        val kakao_black = StepWalkColor(Color(0xFF000000))

        val light_primary = primary
        val light_onPrimary = white
        @Stable
        val light_secondary = grey_300
        val light_onSecondary = lightBlack
        val light_error = red
        @Stable
        val light_onError = StepWalkColor(Color(0xFF410001))
        val light_background = white
        val light_onBackground = lightBlack
        val light_surface = white
        val light_onSurface = lightBlack
        val light_onSurfaceVariant = grey_400
        val light_scrim = grey_600
        val light_outline = grey_600

        val dark_primary = deep_primary
        val dark_onPrimary = lightGrey
        @Stable
        val dark_secondary = deepGrey
        val dark_onSecondary = lightGrey
        @Stable
        val dark_error = StepWalkColor(Color(0xFFFFB4A9))
        val dark_onError = deepRed
        val dark_background = black // 컨테이너 색상
        val dark_onBackground = grey_600
        val dark_surface = lightBlack // 상단바 색상
        val dark_onSurface = grey_600
        val dark_onSurfaceVariant = grey_200
        val dark_scrim = grey_300
        val dark_outline = grey_300
    }
}