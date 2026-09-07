package com.sounddose.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sounddose.ui.theme.SoundDoseTheme
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class SplashParticle(
    val initialOffset: Offset,
    val targetCol: Float,
    val targetRow: Float,
    val size: Float,
    val phase: Float,
    val speed: Float
)

@Composable
fun LandingSplashScreen(
    onAnimationFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SoundDoseTheme.colors
    val progressAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progressAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = FastOutSlowInEasing)
        )
        delay(700)
        onAnimationFinish()
    }

    val letterPatterns = remember {
        mapOf(
            'N' to listOf(
                "10001",
                "11001",
                "10101",
                "10011",
                "10001",
                "10001",
                "10001"
            ),
            'o' to listOf(
                "00000",
                "00000",
                "01110",
                "10001",
                "10001",
                "10001",
                "01110"
            ),
            'i' to listOf(
                "00100",
                "00000",
                "01100",
                "00100",
                "00100",
                "00100",
                "01110"
            ),
            'c' to listOf(
                "00000",
                "00000",
                "01110",
                "10001",
                "10000",
                "10001",
                "01110"
            ),
            'e' to listOf(
                "00000",
                "00000",
                "01110",
                "10001",
                "11111",
                "10000",
                "01110"
            ),
            'S' to listOf(
                "01111",
                "10000",
                "10000",
                "01110",
                "00001",
                "00001",
                "11110"
            ),
            't' to listOf(
                "00100",
                "00100",
                "01110",
                "00100",
                "00100",
                "00101",
                "00010"
            ),
            'a' to listOf(
                "00000",
                "00000",
                "01110",
                "00001",
                "01111",
                "10001",
                "01111"
            ),
            's' to listOf(
                "00000",
                "00000",
                "01110",
                "10000",
                "01110",
                "00001",
                "11110"
            ),
            'h' to listOf(
                "10000",
                "10000",
                "10110",
                "11001",
                "10001",
                "10001",
                "10001"
            )
        )
    }

    val textToForm = "NoiseStash"

    val particles = remember {
        val targets = mutableListOf<Pair<Float, Float>>()
        var colCursor = 0f

        textToForm.forEachIndexed { charIndex, char ->
            val pattern = letterPatterns[char] ?: listOf("11111", "10001", "11111")
            for (row in pattern.indices) {
                val rowStr = pattern[row]
                for (col in rowStr.indices) {
                    if (rowStr[col] == '1') {
                        targets.add(Pair(colCursor + col, row.toFloat()))
                    }
                }
            }
            colCursor += if (char.isUpperCase()) 6.5f else 5.5f
            if (charIndex == 4) colCursor += 1.5f
        }

        val totalCols = colCursor
        val totalRows = 7f
        val random = Random(42)

        targets.map { (c, r) ->
            val angle = random.nextFloat() * 2 * Math.PI.toFloat()
            val radius = 220f + random.nextFloat() * 320f
            val rx = cos(angle) * radius * 1.4f
            val ry = sin(angle) * radius * 0.85f

            SplashParticle(
                initialOffset = Offset(rx, ry),
                targetCol = c - (totalCols / 2f),
                targetRow = r - (totalRows / 2f),
                size = 2.4f + random.nextFloat() * 1.8f,
                phase = random.nextFloat() * 6.28f,
                speed = 0.85f + random.nextFloat() * 0.3f
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onAnimationFinish()
            },
        contentAlignment = Alignment.Center
    ) {
        val currentProgress = progressAnim.value

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            if (w <= 0f || h <= 0f) return@Canvas

            drawRect(color = colors.background)

            val centerX = w / 2f
            val centerY = h / 2f - 20.dp.toPx()
            val dotSpacing = (w / 64f).coerceIn(4.5.dp.toPx(), 7.5.dp.toPx())
            val dotRadiusBase = dotSpacing * 0.32f

            val particleColor = colors.visualizerColor

            particles.forEach { p ->
                val targetX = centerX + p.targetCol * dotSpacing
                val targetY = centerY + p.targetRow * dotSpacing

                val startX = centerX + p.initialOffset.x
                val startY = centerY + p.initialOffset.y

                val midX = centerX + p.targetCol * (dotSpacing * 1.35f) + sin(p.phase) * 30f
                val midY = centerY + (p.targetRow * 0.35f) * dotSpacing + cos(p.phase) * 20f

                val (curX, curY) = when {
                    currentProgress < 0.45f -> {
                        val t = (currentProgress / 0.45f)
                        val curvedT = t * t
                        val x = startX + (midX - startX) * curvedT
                        val y = startY + (midY - startY) * curvedT
                        Pair(x, y)
                    }
                    currentProgress < 0.80f -> {
                        val t = ((currentProgress - 0.45f) / 0.35f)
                        val curvedT = t * (2f - t)
                        val x = midX + (targetX - midX) * curvedT
                        val y = midY + (targetY - midY) * curvedT
                        Pair(x, y)
                    }
                    else -> {
                        val t = ((currentProgress - 0.80f) / 0.20f)
                        val settle = (1f - t) * 2f
                        val x = targetX + sin(p.phase + currentProgress * 4f) * settle
                        val y = targetY + cos(p.phase + currentProgress * 4f) * settle
                        Pair(x, y)
                    }
                }

                val alpha = (0.35f + currentProgress * 0.65f).coerceIn(0.2f, 1.0f)
                val currentRadius = dotRadiusBase * (0.85f + currentProgress * 0.15f)

                drawCircle(
                    color = particleColor.copy(alpha = alpha),
                    radius = currentRadius,
                    center = Offset(curX, curY)
                )
            }
        }

        val taglineAlpha = ((currentProgress - 0.70f) / 0.30f).coerceIn(0f, 1f)
        if (taglineAlpha > 0.05f) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 90.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "hearing is non renewable",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.5.sp,
                    color = colors.textSecondary.copy(alpha = taglineAlpha)
                )
            }
        }
    }
}
