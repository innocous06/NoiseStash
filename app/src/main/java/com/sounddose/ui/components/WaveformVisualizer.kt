package com.sounddose.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sounddose.ui.theme.getDoseColor

@Composable
fun WaveformVisualizer(
    waveformPoints: FloatArray,
    currentDb: Double,
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    val doseColor = getDoseColor((currentDb - 45.0).coerceIn(0.0, 100.0))
    val animatedColor by animateColorAsState(
        targetValue = if (isListening) doseColor else Color(0xFF334155),
        animationSpec = tween(durationMillis = 200),
        label = "wave_color"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
    ) {
        val count = waveformPoints.size.coerceAtLeast(1)
        val spacing = size.width / count
        val barWidth = spacing * 0.50f
        val centerY = size.height / 2f

        for (i in 0 until count) {
            val amp = if (isListening) waveformPoints[i].coerceIn(0.05f, 1.0f) else 0.05f
            val barH = (size.height * amp * 0.85f).coerceAtLeast(3.dp.toPx())

            val left = (i * spacing) + (spacing - barWidth) / 2f
            val top = centerY - (barH / 2f)

            // Fades from bright top down into the black background
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(
                        animatedColor.copy(alpha = 0.90f),
                        animatedColor.copy(alpha = 0.20f)
                    ),
                    startY = top,
                    endY = top + barH
                ),
                topLeft = Offset(left, top),
                size = Size(barWidth, barH),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}
