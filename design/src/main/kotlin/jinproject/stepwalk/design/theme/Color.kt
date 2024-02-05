package jinproject.stepwalk.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Immutable
@JvmInline
value class StepWalkColor private constructor(val color: Color) {
    companion object {
        @Stable
        private val lightBlack = StepWalkColor(Color(0xFF1D1D1D))

        @Stable
        private val mediumBlack = StepWalkColor(Color(0xFF131313))

        @Stable
        private val black = StepWalkColor(Color(0xFF000000))

        @Stable
        private val white = StepWalkColor(Color(0XFFFFFFFF))


        @Stable
        val red_50 = StepWalkColor(Color(0xFFFFEBEE))

        @Stable
        val red_100 = StepWalkColor(Color(0xFFFFCDD2))

        @Stable
        val red_200 = StepWalkColor(Color(0xFFEF9A9A))

        @Stable
        val red_300 = StepWalkColor(Color(0xFFE57373))

        @Stable
        val red_400 = StepWalkColor(Color(0xFFEF5350))

        @Stable
        val red_500 = StepWalkColor(Color(0xFFF44336))

        @Stable
        val red_600 = StepWalkColor(Color(0xFFE53935))

        @Stable
        val red_700 = StepWalkColor(Color(0xFFD32F2F))

        @Stable
        val red_800 = StepWalkColor(Color(0xFFC62828))

        @Stable
        val red_900 = StepWalkColor(Color(0xFFB71C1C))

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
        val orange_700 = StepWalkColor(Color(0xFFF57C00))

        @Stable
        val orange_800 = StepWalkColor(Color(0xFFEF6C00))

        @Stable
        val green_100 = StepWalkColor(Color(0xFFC8E6C9))

        @Stable
        val green_200 = StepWalkColor(Color(0xFFA5D6A7))

        @Stable
        val green_300 = StepWalkColor(Color(0xFF81C784))

        @Stable
        val green_400 = StepWalkColor(Color(0xFF66BB6A))

        @Stable
        val green_500 = StepWalkColor(Color(0xFF4CAF50))

        @Stable
        val green_600 = StepWalkColor(Color(0xFF43A047))

        @Stable
        val green_700 = StepWalkColor(Color(0xFF388E3C))

        @Stable
        val green_800 = StepWalkColor(Color(0xFF2E7D32))

        @Stable
        val green_900 = StepWalkColor(Color(0xFF1B5E20))

        @Stable
        private val grey_50 = StepWalkColor(Color(0xFFFAFAFA))

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
        val blueGrey_50 = StepWalkColor(Color(0xFFECEFF1))

        @Stable
        val blueGrey_100 = StepWalkColor(Color(0xFFCFD8DC))

        @Stable
        val blueGrey_200 = StepWalkColor(Color(0xFFB0BEC5))

        @Stable
        val blueGrey_300 = StepWalkColor(Color(0xFF90A4AE))

        @Stable
        val blueGrey_400 = StepWalkColor(Color(0xFF78909C))

        @Stable
        val blueGrey_500 = StepWalkColor(Color(0xFF607D8B))

        @Stable
        val blueGrey_600 = StepWalkColor(Color(0xFF546E7A))

        @Stable
        val blueGrey_700 = StepWalkColor(Color(0xFF455A64))

        @Stable
        val blueGrey_800 = StepWalkColor(Color(0xFF37474F))

        @Stable
        val blueGrey_900 = StepWalkColor(Color(0xFF263238))

        @Stable
        val kakao_yellow = StepWalkColor(Color(0xFFFEE500))

        @Stable
        val kakao_black = StepWalkColor(Color(0xFF000000))

        /*
        Light Theme
         */
        val light_primary = green_300
        val light_onPrimary = white
        val light_primaryContainer = green_100
        val light_onPrimaryContainer = green_900
        val light_secondary = blueGrey_300
        val light_onSecondary = white
        val light_secondaryContainer = blueGrey_100
        val light_onSecondaryContainer = blueGrey_700
        val light_error = red_800
        val light_onError = white
        val light_background = grey_50
        val light_onBackground = lightBlack
        val light_surface = white
        val light_surfaceVariant = grey_200
        val light_onSurface = lightBlack
        val light_onSurfaceVariant = grey_600
        val light_outline = grey_700
        val light_outlineVariant = grey_400

        /*
        Dark Theme
         */
        val dark_primary = green_300
        val dark_onPrimary = white
        val dark_primaryContainer = green_100
        val dark_onPrimaryContainer = green_900
        val dark_secondary = blueGrey_300
        val dark_onSecondary = white
        val dark_secondaryContainer = blueGrey_100
        val dark_onSecondaryContainer = blueGrey_700
        val dark_error = red_800
        val dark_onError = white
        val dark_background = black
        val dark_onBackground = grey_600
        val dark_surface = lightBlack
        val dark_surfaceVariant = mediumBlack
        val dark_onSurface = grey_600
        val dark_onSurfaceVariant = grey_200
        val dark_outline = grey_400
        val dark_outlineVariant = grey_700
    }
}