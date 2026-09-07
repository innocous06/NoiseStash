package com.sounddose.ui.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sounddose.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DoseRing(
    dosePercent: Double,
    safeTimeRemainingHours: Double,
    budgetMultiplier: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    val totalBudget = 100.0 * budgetMultiplier
    val targetProgress = (dosePercent / totalBudget).coerceIn(0.0, 1.5).toFloat()

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 650),
        label = "ring_progress"
    )

    val ringColor = getDoseColor(dosePercent)
    val animatedColor by animateColorAsState(
        targetValue = ringColor,
        animationSpec = tween(durationMillis = 350),
        label = "ring_color"
    )

    val colors = SoundDoseTheme.colors

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image 1 Style Hero Ring with Inner Radial Ticks
        Box(
            modifier = Modifier.size(236.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                val strokeWidth = 16.dp.toPx()
                val arcSize = Size(size.width, size.height)
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.width / 2f

                // Soft radial halo smudging outward into the background
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(animatedColor.copy(alpha = if (colors.isDark) 0.16f else 0.22f), Color.Transparent),
                        center = center,
                        radius = radius + 24.dp.toPx()
                    )
                )

                // Outer track
                val trackColor = if (colors.isDark) Color(0x18FFFFFF) else Color(0x2278716C)
                drawArc(
                    color = trackColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    size = arcSize
                )

                // Inner Radial Ticks (Image 1 reference)
                val innerRadius = radius - strokeWidth - 8.dp.toPx()
                val tickLength = 4.dp.toPx()
                val tickCount = 48
                val tickColor = if (colors.isDark) Color(0x25FFFFFF) else Color(0x3078716C)

                for (i in 0 until tickCount) {
                    val angleRad = Math.toRadians((i * (360f / tickCount) - 90).toDouble())
                    val startX = center.x + ((innerRadius - tickLength) * cos(angleRad)).toFloat()
                    val startY = center.y + ((innerRadius - tickLength) * sin(angleRad)).toFloat()
                    val endX = center.x + (innerRadius * cos(angleRad)).toFloat()
                    val endY = center.y + (innerRadius * sin(angleRad)).toFloat()

                    drawLine(
                        color = tickColor,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 1.2.dp.toPx()
                    )
                }

                // Fluid progress arc
                val sweep = (animatedProgress * 360f).coerceAtLeast(2f)
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(
                            animatedColor.copy(alpha = 0.50f),
                            animatedColor,
                            animatedColor
                        )
                    ),
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    size = arcSize
                )
            }

            // Inner Center Metric (Image 1 Style: Bold % & DAILY DOSE)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${"%.0f".format(dosePercent)}%",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp,
                    color = colors.textPrimary
                )
                Text(
                    text = "DAILY DOSE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Safe Listening Budget Left & Linear Bar
        val remainingFormatted = when {
            dosePercent >= totalBudget -> "0 min"
            safeTimeRemainingHours <= 0.0 -> "0 min"
            safeTimeRemainingHours >= 1.0 -> {
                val hrs = safeTimeRemainingHours.toInt()
                val mins = ((safeTimeRemainingHours - hrs) * 60).toInt()
                "${hrs} h ${mins} m"
            }
            else -> {
                val mins = (safeTimeRemainingHours * 60).toInt().coerceAtLeast(1)
                "${mins} mins"
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = remainingFormatted,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "SAFE LISTENING BUDGET LEFT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Horizontal Linear Bar
            val budgetFraction = ((totalBudget - dosePercent) / totalBudget).coerceIn(0.0, 1.0).toFloat()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (colors.isDark) Color(0x18FFFFFF) else Color(0x2278716C))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(budgetFraction)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(animatedColor)
                )
            }
        }
    }
}
