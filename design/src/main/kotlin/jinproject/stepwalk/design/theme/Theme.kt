package jinproject.stepwalk.design.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.dark_background
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.dark_error
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.dark_onBackground
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.dark_onError
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.dark_onPrimary
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.dark_onPrimaryContainer
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.dark_onSecondary
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.dark_onSecondaryContainer
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.dark_onSurface
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.dark_onSurfaceVariant
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.dark_outline
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.dark_outlineVariant
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.dark_primary
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.dark_primaryContainer
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.dark_secondary
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.dark_secondaryContainer
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.dark_surface
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.dark_surfaceVariant
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.light_background
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.light_error
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.light_onBackground
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.light_onError
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.light_onPrimary
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.light_onPrimaryContainer
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.light_onSecondary
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.light_onSecondaryContainer
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.light_onSurface
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.light_onSurfaceVariant
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.light_outline
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.light_outlineVariant
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.light_primary
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.light_primaryContainer
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.light_secondary
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.light_secondaryContainer
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.light_surface
import jinproject.stepwalk.design.theme.StepWalkColor.Companion.light_surfaceVariant

/**
 * # History
 * > https://m3.material.io/styles/color/roles Material3 의 color roles 을 따르되, material2 의 color scheme 을 적용
 *
 * # Color Role
 * - primary: surface 위의 버튼, 프로그레스바, 레이블, 체크박스등 같은 컴포넌트의 색상
 * - primaryContainer: surface 위의 FAB 같이 좀 더 위의 계층에 있어 우선순위가 높은 컴포넌트의 색상
 * - secondary: surface 위의 primary 보다 우선순위가 떨어지는 컴포넌트 의 Filled 색상
 * - secondaryContainer: primaryContainer 보다 우선순위가 떨어지는 컴포넌트 (Tonal button(바닥에 꺼져있는 듯한 버튼) = bottom Navigation Bar의 indicator) 의 색상
 * - background: 화면의 Root background 색상
 * - surface: topBar, bottomBar, card, sheets, dialog 같은 background 위의 컴포넌트 요소의 filled color
 * - error: Error 상태의 filled color
 * - outline: 색상이 surface 와 일치하는 (텍스트필드, 버튼 등) 요소 의 boundary(외곽으로써 안과 밖의 경계를 표현) 색상
 * - outlineVariant: 장식 요소의 색상(divier 같은)
 *
 * # Naming Rule
 * - onXXX: XXX color 위의 요소의 색상
 * - XXXVariant: XXX color 와 대비하여 강조하고 싶은 요소의 색상
 * - XXXContainer: XXX color 의 요소 보다 view hierarchy 상에서 좀 더 위에 존재 하는 요소의 filled 색상
 */
@Stable
private val LightColorScheme = lightColorScheme(
    primary = light_primary.color,
    onPrimary = light_onPrimary.color,
    primaryContainer = light_primaryContainer.color,
    onPrimaryContainer = light_onPrimaryContainer.color,
    secondary = light_secondary.color,
    onSecondary = light_onSecondary.color,
    secondaryContainer = light_secondaryContainer.color,
    onSecondaryContainer = light_onSecondaryContainer.color,
    background = light_background.color,
    onBackground = light_onBackground.color,
    surface = light_surface.color,
    surfaceVariant = light_surfaceVariant.color,
    onSurface = light_onSurface.color,
    onSurfaceVariant = light_onSurfaceVariant.color,
    error = light_error.color,
    onError = light_onError.color,
    outline = light_outline.color,
    outlineVariant = light_outlineVariant.color,
)

@Stable
private val DarkColorScheme = darkColorScheme(
    primary = dark_primary.color,
    onPrimary = dark_onPrimary.color,
    primaryContainer = dark_primaryContainer.color,
    onPrimaryContainer = dark_onPrimaryContainer.color,
    secondary = dark_secondary.color,
    onSecondary = dark_onSecondary.color,
    secondaryContainer = dark_secondaryContainer.color,
    onSecondaryContainer = dark_onSecondaryContainer.color,
    background = dark_background.color,
    onBackground = dark_onBackground.color,
    surface = dark_surface.color,
    surfaceVariant = dark_surfaceVariant.color,
    onSurface = dark_onSurface.color,
    onSurfaceVariant = dark_onSurfaceVariant.color,
    error = dark_error.color,
    onError = dark_onError.color,
    outline = dark_outline.color,
    outlineVariant = dark_outlineVariant.color,
)

@Composable
fun StepWalkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
                !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}