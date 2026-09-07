package com.sounddose.ui.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sounddose.data.DailyDoseSummary
import com.sounddose.ui.components.AmbientAudioBackground
import com.sounddose.ui.components.SmudgedCard
import com.sounddose.ui.theme.*

@Composable
fun HistoryScreen(
    dailySummaries: List<DailyDoseSummary>
) {
    val scrollState = rememberScrollState()
    val colors = SoundDoseTheme.colors

    val summaries = if (dailySummaries.isNotEmpty()) {
        dailySummaries
    } else {
        listOf(
            DailyDoseSummary("Mon", 92.0, 24),
            DailyDoseSummary("Tue", 78.0, 24),
            DailyDoseSummary("Wed", 105.0, 26),
            DailyDoseSummary("Thu", 64.0, 22),
            DailyDoseSummary("Fri", 88.0, 25),
            DailyDoseSummary("Sat", 18.0, 10),
            DailyDoseSummary("Sun", 42.0, 14)
        )
    }

    var selectedIndex by remember { mutableStateOf(summaries.size - 1) }
    val maxDose = (summaries.maxOfOrNull { it.totalDose } ?: 100.0).coerceAtLeast(110.0)
    val avgDose = if (summaries.isNotEmpty()) summaries.map { it.totalDose }.average() else 0.0
    val worstDay = summaries.maxByOrNull { it.totalDose }
    val quietestDay = summaries.minByOrNull { it.totalDose }

    Box(modifier = Modifier.fillMaxSize()) {
        AmbientAudioBackground(
            waveformPoints = FloatArray(24) { 0.06f },
            currentDb = 40.0,
            isListening = false
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "7-Day Exposure",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                color = colors.textPrimary
            )
            Text(
                text = "Longitudinal cumulative dose tracking",
                fontSize = 12.sp,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

        // 7-Day Smudged Bar Chart Container
        SmudgedCard(
            cornerRadius = 24.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DAILY LOAD (% LIMIT)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TextSecondary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(DoseDanger)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "100% NIOSH Limit",
                            fontSize = 10.sp,
                            color = DoseDanger,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                SevenDayBarChart(
                    summaries = summaries,
                    maxScale = maxDose,
                    selectedIndex = selectedIndex,
                    onSelectBar = { selectedIndex = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "CLINICAL RISK METRICS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = colors.textSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Metric 1: Peak Risk Day
        worstDay?.let { worst ->
            SmudgedCard(
                cornerRadius = 16.dp,
                accentGlow = DoseDanger,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = null,
                                tint = DoseDanger,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Peak Exposure (${worst.date})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }
                        Text(
                            text = "${"%.0f".format(worst.totalDose)}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DoseDanger
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Crossed safe daily ceiling driven by heavy traffic congestion and frequent horn blasts.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Metric 2: Weekly Average Dose
        SmudgedCard(
            cornerRadius = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Analytics,
                            contentDescription = null,
                            tint = getDoseColor(avgDose),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Weekly Average Dose",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                    Text(
                        text = "${"%.1f".format(avgDose)}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = getDoseColor(avgDose)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Recommended safe exposure target is <50% daily dose to prevent early-onset sensorineural hearing loss.",
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Metric 3: Best / Recovery Day
        quietestDay?.let { best ->
            SmudgedCard(
                cornerRadius = 16.dp,
                accentGlow = DoseSafe,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Shield,
                                contentDescription = null,
                                tint = DoseSafe,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Acoustic Recovery (${best.date})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }
                        Text(
                            text = "${"%.0f".format(best.totalDose)}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DoseSafe
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Auditory hair cells successfully rested in quiet ambient conditions below 65 dB.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
}

@Composable
fun SevenDayBarChart(
    summaries: List<DailyDoseSummary>,
    maxScale: Double,
    selectedIndex: Int,
    onSelectBar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SoundDoseTheme.colors

    Canvas(modifier = modifier) {
        val chartWidth = size.width
        val chartHeight = size.height - 28.dp.toPx()
        val barCount = summaries.size
        val barSpacing = chartWidth / (barCount.coerceAtLeast(1))
        val barWidth = barSpacing * 0.48f

        // Draw 100% threshold dashed red line
        val limitY = chartHeight * (1f - (100.0 / maxScale).toFloat())
        drawLine(
            color = DoseDanger.copy(alpha = 0.45f),
            start = Offset(0f, limitY),
            end = Offset(chartWidth, limitY),
            strokeWidth = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
        )

        // Draw 50% safe threshold dotted green line
        val safe50Y = chartHeight * (1f - (50.0 / maxScale).toFloat())
        drawLine(
            color = DoseSafe.copy(alpha = 0.25f),
            start = Offset(0f, safe50Y),
            end = Offset(chartWidth, safe50Y),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
        )

        // Draw Bars
        for (i in 0 until barCount) {
            val item = summaries[i]
            val barHeightFraction = (item.totalDose / maxScale).coerceIn(0.04, 1.0).toFloat()
            val barH = chartHeight * barHeightFraction
            val left = (i * barSpacing) + (barSpacing - barWidth) / 2f
            val top = chartHeight - barH
            val barColor = getDoseColor(item.totalDose)

            drawRoundRect(
                color = barColor,
                topLeft = Offset(left, top),
                size = Size(barWidth, barH),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        summaries.forEachIndexed { idx, item ->
            val label = if (item.date.length > 5) item.date.takeLast(2) else item.date
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(36.dp)
                    .clickable { onSelectBar(idx) }
            ) {
                Text(
                    text = "${item.totalDose.toInt()}%",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = getDoseColor(item.totalDose)
                )
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = if (selectedIndex == idx) colors.textPrimary else colors.textSecondary,
                    fontWeight = if (selectedIndex == idx) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}
