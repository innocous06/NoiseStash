package com.sounddose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

@Immutable
data class SoundDoseColors(
    val isDark: Boolean,
    val background: Color,
    val backgroundElevated: Color,
    val visualizerColor: Color,
    val visualizerGlow: Color,
    val lineArtColor: Color,
    val cardBackground: Color,
    val cardBackgroundElevated: Color,
    val cardBorder: Color,
    val cardBorderSubtle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val pillActiveBg: Color,
    val pillActiveText: Color,
    val pillInactiveBorder: Color,
    val pillInactiveText: Color,
    val bottomBarBg: Color,
    val bottomBarBorder: Color,
    val bottomBarActive: Color,
    val bottomBarInactive: Color
)

val DarkSoundDoseColors = SoundDoseColors(
    isDark = true,
    background = DarkCharcoalBg,
    backgroundElevated = DarkCharcoalBgElevated,
    visualizerColor = DarkVisualizerSilver,
    visualizerGlow = DarkVisualizerSilverGlow,
    lineArtColor = DarkLineArtSilver,
    cardBackground = DarkSmudgeCard,
    cardBackgroundElevated = DarkSmudgeCardElevated,
    cardBorder = DarkSmudgeBorder,
    cardBorderSubtle = DarkSmudgeBorderSubtle,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textTertiary = DarkTextTertiary,
    pillActiveBg = DarkPillActiveBg,
    pillActiveText = DarkPillActiveText,
    pillInactiveBorder = DarkPillInactiveBorder,
    pillInactiveText = DarkPillInactiveText,
    bottomBarBg = DarkCharcoalBg,
    bottomBarBorder = Color(0x18FFFFFF),
    bottomBarActive = Color(0xFFFFFFFF),
    bottomBarInactive = Color(0xFF64748B)
)

val LightSoundDoseColors = SoundDoseColors(
    isDark = false,
    background = LightAmberBg,
    backgroundElevated = LightAmberBgElevated,
    visualizerColor = LightVisualizerOrange,
    visualizerGlow = LightVisualizerAmber,
    lineArtColor = LightLineArtOrange,
    cardBackground = LightSmudgeCard,
    cardBackgroundElevated = LightSmudgeCardElevated,
    cardBorder = LightSmudgeBorder,
    cardBorderSubtle = LightSmudgeBorderSubtle,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textTertiary = LightTextTertiary,
    pillActiveBg = LightPillActiveBg,
    pillActiveText = LightPillActiveText,
    pillInactiveBorder = LightPillInactiveBorder,
    pillInactiveText = LightPillInactiveText,
    bottomBarBg = Color(0xFFFEF9E7),
    bottomBarBorder = Color(0x30E2D4B7),
    bottomBarActive = Color(0xFF1C1917),
    bottomBarInactive = Color(0xFF78716C)
)

val LocalSoundDoseColors = staticCompositionLocalOf { DarkSoundDoseColors }
val LocalThemeToggle = staticCompositionLocalOf<() -> Unit> { {} }

object SoundDoseTheme {
    val colors: SoundDoseColors
        @Composable
        get() = LocalSoundDoseColors.current
}

private val DarkMaterialScheme = darkColorScheme(
    primary = DoseSafe,
    background = DarkCharcoalBg,
    surface = DarkCharcoalBgElevated,
    onPrimary = Color.Black,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary
)

private val LightMaterialScheme = lightColorScheme(
    primary = LightVisualizerOrange,
    background = LightAmberBg,
    surface = LightAmberBgElevated,
    onPrimary = Color.White,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary
)

@Composable
fun SoundDoseTheme(
    isDark: Boolean = true,
    onToggleTheme: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val soundDoseColors = if (isDark) DarkSoundDoseColors else LightSoundDoseColors
    val materialScheme = if (isDark) DarkMaterialScheme else LightMaterialScheme

    CompositionLocalProvider(
        LocalSoundDoseColors provides soundDoseColors,
        LocalThemeToggle provides onToggleTheme
    ) {
        MaterialTheme(
            colorScheme = materialScheme,
            content = content
        )
    }
}
