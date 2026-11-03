package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.data.model.AppThemeMode

data class LuxePalette(
    val isDark: Boolean,
    val isNightTransferLowLight: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val card: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val goldPrimary: Color,
    val goldLight: Color,
    val goldDark: Color,
    val goldSecondary: Color,
    val accentBlue: Color,
    val accentGreen: Color,
    val accentRed: Color,
    val accentAmber: Color,
    val accentPurple: Color,
    val armoredBadge: Color
)

val NightTransferPalette = LuxePalette(
    isDark = true,
    isNightTransferLowLight = true,
    background = ObsidianBackground,
    surface = ObsidianSurface,
    surfaceVariant = ObsidianSurfaceVariant,
    card = ObsidianCard,
    border = ObsidianBorder,
    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    textTertiary = TextTertiary,
    goldPrimary = GoldPrimary,
    goldLight = GoldLight,
    goldDark = GoldDark,
    goldSecondary = GoldSecondary,
    accentBlue = AccentBlue,
    accentGreen = AccentGreen,
    accentRed = AccentRed,
    accentAmber = AccentAmber,
    accentPurple = AccentPurple,
    armoredBadge = ArmoredBadgeColor
)

val ChampagneLightPalette = LuxePalette(
    isDark = false,
    isNightTransferLowLight = false,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    card = LightCard,
    border = LightBorder,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textTertiary = LightTextTertiary,
    goldPrimary = ChampagnePrimaryLight,
    goldLight = GoldPrimary,
    goldDark = GoldDark,
    goldSecondary = GoldSecondary,
    accentBlue = AccentBlue,
    accentGreen = AccentGreen,
    accentRed = AccentRed,
    accentAmber = AccentAmber,
    accentPurple = AccentPurple,
    armoredBadge = ArmoredBadgeColor
)

val LocalLuxePalette = staticCompositionLocalOf { NightTransferPalette }

object LuxeTheme {
    val palette: LuxePalette
        @Composable
        get() = LocalLuxePalette.current
}

private val LuxuryDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = ObsidianBackground,
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,
    secondary = GoldSecondary,
    onSecondary = ObsidianBackground,
    secondaryContainer = ObsidianSurfaceVariant,
    onSecondaryContainer = VipPlatinum,
    tertiary = AccentBlue,
    onTertiary = ObsidianBackground,
    background = ObsidianBackground,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = ObsidianBorder,
    error = AccentRed,
    onError = TextPrimary
)

private val LuxuryLightColorScheme = lightColorScheme(
    primary = ChampagnePrimaryLight,
    onPrimary = Color.White,
    primaryContainer = GoldLight,
    onPrimaryContainer = GoldDark,
    secondary = GoldSecondary,
    onSecondary = Color.White,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = LightTextPrimary,
    tertiary = AccentBlue,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = AccentRed,
    onError = Color.White
)

@Composable
fun LuxeDriveTheme(
    themeMode: AppThemeMode = AppThemeMode.NIGHT_TRANSFER,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.NIGHT_TRANSFER -> true
        AppThemeMode.CHAMPAGNE_LIGHT -> false
        AppThemeMode.SYSTEM_AUTO -> systemDark
    }

    val palette = if (isDark) NightTransferPalette else ChampagneLightPalette
    val colorScheme = if (isDark) LuxuryDarkColorScheme else LuxuryLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = palette.background.toArgb()
            window.navigationBarColor = palette.surface.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !isDark
            insetsController.isAppearanceLightNavigationBars = !isDark
        }
    }

    CompositionLocalProvider(LocalLuxePalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
