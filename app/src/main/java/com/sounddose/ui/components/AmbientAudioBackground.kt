package com.sounddose.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.sounddose.ui.theme.SoundDoseTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * AmbientAudioBackground renders:
 * 1. Background Canvas Fill (Light Yellow Amber or Charcoal Black)
 * 2. Algorithmic Animated Line Art: Harmonic wave superposition flowing with real audio energy
 * 3. Smoothed Ambient Sound Visualizer: Vertical audio bars rising & shrinking gracefully
 *
 * Placed behind all content layers, smudging seamlessly into cards and text.
 */
@Composable
fun AmbientAudioBackground(
    waveformPoints: FloatArray,
    currentDb: Double,
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = SoundDoseTheme.colors
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_anim")

    // Continuous time ticker for organic fluid motion
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_loop"
    )

    // Secondary subtle wave counter-phase
    val animTimeFast by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (4 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_counter"
    )

    // Audio energy factor (normalizes 30-85 dB into 0.12 to 1.0)
    val targetAudioEnergy = if (isListening) {
        ((currentDb - 30.0) / 55.0).coerceIn(0.12, 1.0).toFloat()
    } else {
        0.10f
    }

    val smoothedEnergy by animateFloatAsState(
        targetValue = targetAudioEnergy,
        animationSpec = spring(dampingRatio = 0.60f, stiffness = 140f),
        label = "energy_smooth"
    )

    // Damped bar height tracking for 38 vertical bars
    val barCount = 38
    val smoothedBars = remember { FloatArray(barCount) { 0.10f } }

    // Update bar heights with low-pass exponential smoothing
    LaunchedEffect(waveformPoints, isListening, smoothedEnergy) {
        val inputCount = waveformPoints.size.coerceAtLeast(1)
        for (i in 0 until barCount) {
            val ratio = i.toFloat() / (barCount - 1).coerceAtLeast(1)
            val sampleIdx = (ratio * (inputCount - 1)).toInt().coerceIn(0, inputCount - 1)
            val rawAmp = if (isListening) waveformPoints[sampleIdx] else 0.04f

            // Natural window curve (taper outer edges, elevate center)
            val window = (sin(ratio * PI.toFloat()) * 0.35f + 0.65f)

            // Boost raw PCM amplitude so physical mic variations have noticeable flutter
            val boostedWave = (rawAmp * 5.0f).coerceIn(0.15f, 1.0f)

            // Combined energy ensures bars rise noticeably with dB while fluttering with waveform
            val combined = (smoothedEnergy * 0.70f + boostedWave * smoothedEnergy * 0.30f)
            val targetHeight = (combined * window).coerceIn(0.08f, 1.0f)

            // Snappy yet smooth lerp
            smoothedBars[i] = smoothedBars[i] * 0.50f + targetHeight * 0.50f
        }
    }

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        // 1. Draw base ambient background
        drawRect(color = colors.background)

        // 2. Algorithmic Generative Line Art (Harmonic sinusoidal superposition)
        val lineArtColor = colors.lineArtColor
        val numRibbons = 3

        for (r in 0 until numRibbons) {
            val path = Path()
            val baseProgress = r.toFloat() / numRibbons
            val baseY = h * (0.22f + baseProgress * 0.28f)
            val ribbonPhase = animTime + r * 1.8f
            val ribbonAmp = (h * 0.045f) * (0.8f + smoothedEnergy * 1.4f)

            var first = true
            val stepPx = 16f
            var x = 0f

            while (x <= w + stepPx) {
                val normX = x / w
                val wave1 = sin((normX * 2.8f * PI.toFloat()) + ribbonPhase)
                val wave2 = cos((normX * 1.6f * PI.toFloat()) - animTimeFast * 0.5f + r)
                val wave3 = sin((normX * 5.2f * PI.toFloat()) + ribbonPhase * 1.5f) * 0.25f

                val y = baseY + (wave1 * wave2 + wave3) * ribbonAmp

                if (first) {
                    path.moveTo(x, y)
                    first = false
                } else {
                    path.lineTo(x, y)
                }
                x += stepPx
            }

            val alpha = (0.16f + (1f - baseProgress) * 0.14f) * (0.7f + smoothedEnergy * 0.5f)
            drawPath(
                path = path,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        lineArtColor.copy(alpha = 0.02f),
                        lineArtColor.copy(alpha = alpha.coerceIn(0.05f, 0.45f)),
                        lineArtColor.copy(alpha = (alpha * 0.85f).coerceIn(0.05f, 0.45f)),
                        lineArtColor.copy(alpha = 0.02f)
                    )
                ),
                style = Stroke(
                    width = (1.5f + r * 0.5f).dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }

        // 3. Smoothed Background Sound Visualizer Bars (Reaching up to 58% screen height)
        val barSpacing = w / barCount
        val barWidth = barSpacing * 0.58f
        val maxBarHeight = h * 0.58f
        val visColor = colors.visualizerColor

        for (i in 0 until barCount) {
            val amp = smoothedBars[i].coerceIn(0.08f, 1.0f)
            val barH = (maxBarHeight * amp).coerceAtLeast(10.dp.toPx())
            val left = i * barSpacing + (barSpacing - barWidth) / 2f
            val top = h - barH

            val topAlpha = if (colors.isDark) 0.40f else 0.50f
            val bottomAlpha = 0.04f

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        visColor.copy(alpha = topAlpha * (0.6f + amp * 0.4f)),
                        visColor.copy(alpha = topAlpha * 0.5f),
                        visColor.copy(alpha = bottomAlpha)
                    ),
                    startY = top,
                    endY = h
                ),
                topLeft = Offset(left, top),
                size = Size(barWidth, barH),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}
