package com.sounddose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sounddose.ui.theme.SoundDoseTheme

@Composable
fun SmudgedCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    accentGlow: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = SoundDoseTheme.colors
    val shape = RoundedCornerShape(cornerRadius)

    val backgroundBrush = if (colors.isDark) {
        if (accentGlow != null) {
            Brush.verticalGradient(
                listOf(
                    accentGlow.copy(alpha = 0.14f),
                    colors.cardBackground,
                    colors.cardBackground.copy(alpha = 0.03f)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    colors.cardBackgroundElevated,
                    colors.cardBackground
                )
            )
        }
    } else {
        // Light Mode: warm frosted amber-white card
        if (accentGlow != null) {
            Brush.verticalGradient(
                listOf(
                    accentGlow.copy(alpha = 0.12f),
                    colors.cardBackground,
                    colors.cardBackground
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    colors.cardBackground,
                    colors.cardBackground.copy(alpha = 0.85f)
                )
            )
        }
    }

    val borderBrush = if (colors.isDark) {
        if (accentGlow != null) {
            Brush.verticalGradient(
                listOf(
                    accentGlow.copy(alpha = 0.45f),
                    colors.cardBorderSubtle,
                    Color.Transparent
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    colors.cardBorder,
                    colors.cardBorderSubtle,
                    Color.Transparent
                )
            )
        }
    } else {
        if (accentGlow != null) {
            Brush.verticalGradient(
                listOf(
                    accentGlow.copy(alpha = 0.35f),
                    colors.cardBorder,
                    colors.cardBorderSubtle
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    colors.cardBorder,
                    colors.cardBorderSubtle
                )
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundBrush)
            .border(1.dp, borderBrush, shape),
        content = content
    )
}
