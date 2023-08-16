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
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.dark_background
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.dark_error
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.dark_onBackground
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.dark_onError
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.dark_onPrimary
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.dark_onSecondary
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.dark_onSurface
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.dark_outline
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.dark_primary
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.dark_scrim
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.dark_secondary
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.dark_surface
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.light_background
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.light_error
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.light_onBackground
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.light_onError
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.light_onPrimary
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.light_onSecondary
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.light_onSurface
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.light_outline
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.light_primary
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.light_scrim
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.light_secondary
import jinproject.stepwalk.design.theme.MiscellaneousToolColor.Companion.light_surface

@Stable
private val DarkColorScheme = darkColorScheme(
    primary = dark_primary.color,
    onPrimary = dark_onPrimary.color,
    secondary = dark_secondary.color,
    onSecondary = dark_onSecondary.color,
    background = dark_background.color,
    onBackground = dark_onBackground.color,
    surface = dark_surface.color,
    onSurface = dark_onSurface.color,
    error = dark_error.color,
    onError = dark_onError.color,
    scrim = dark_scrim.color,
    outline = dark_outline.color
)

@Stable
private val LightColorScheme = lightColorScheme(
    primary = light_primary.color,
    onPrimary = light_onPrimary.color,
    secondary = light_secondary.color,
    onSecondary = light_onSecondary.color,
    background = light_background.color,
    onBackground = light_onBackground.color,
    surface = light_surface.color,
    onSurface = light_onSurface.color,
    error = light_error.color,
    onError = light_onError.color,
    scrim = light_scrim.color,
    outline = light_outline.color
)

@Composable
fun StepWalkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
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
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}