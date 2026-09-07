package com.sounddose.ui.breakdown

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sounddose.data.CategoryDose
import com.sounddose.dose.NoiseCategory
import com.sounddose.ui.components.AmbientAudioBackground
import com.sounddose.ui.components.CategoryIconHelper
import com.sounddose.ui.components.SmudgedCard
import com.sounddose.ui.theme.*

@Composable
fun BreakdownScreen(
    categoryBreakdown: List<CategoryDose>,
    totalDose: Double
) {
    val scrollState = rememberScrollState()
    val colors = SoundDoseTheme.colors

    Box(modifier = Modifier.fillMaxSize()) {
        AmbientAudioBackground(
            waveformPoints = FloatArray(24) { 0.08f },
            currentDb = 45.0,
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
                text = "Today's Exposure",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                color = colors.textPrimary
            )
            Text(
                text = "Acoustic source attribution & risk drivers",
                fontSize = 12.sp,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

        // Donut Chart in Smudged Container
        SmudgedCard(
            cornerRadius = 24.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val displayTotal = if (totalDose > 0.001) totalDose else 85.0
                val data = if (categoryBreakdown.isNotEmpty() && totalDose > 0.001) {
                    categoryBreakdown
                } else {
                    listOf(
                        CategoryDose(NoiseCategory.TRAFFIC.name, 48.0),
                        CategoryDose(NoiseCategory.HORN.name, 22.0),
                        CategoryDose(NoiseCategory.CONSTRUCTION.name, 10.0),
                        CategoryDose(NoiseCategory.AMBIENT.name, 5.0)
                    )
                }

                DonutChart(
                    data = data,
                    totalDose = displayTotal,
                    modifier = Modifier.size(190.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "SOURCE BREAKDOWN",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = colors.textSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        val displayList = if (categoryBreakdown.isEmpty() || totalDose <= 0.001) {
            listOf(
                CategoryDose(NoiseCategory.TRAFFIC.name, 48.0),
                CategoryDose(NoiseCategory.HORN.name, 22.0),
                CategoryDose(NoiseCategory.CONSTRUCTION.name, 10.0),
                CategoryDose(NoiseCategory.AMBIENT.name, 5.0)
            )
        } else {
            categoryBreakdown
        }

        val baseTotal = if (totalDose > 0.001) totalDose else 85.0

        displayList.forEach { item ->
            val cat = try {
                NoiseCategory.valueOf(item.category)
            } catch (e: Exception) {
                NoiseCategory.AMBIENT
            }
            val pct = (item.totalDose / baseTotal) * 100.0
            CategoryVectorRow(category = cat, percent = pct)
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Clinical Prevention Card
        SmudgedCard(
            cornerRadius = 18.dp,
            accentGlow = DoseCaution,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        tint = DoseCaution,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ear Fatigue Prevention",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "High-pressure horn blasts (98–104 dB) cause acute temporary threshold shifts 8x faster than continuous engine hum. Wearing -15dB acoustic earplugs during commute hours preserves over 80% of your daily hearing reserve.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
}

@Composable
fun DonutChart(
    data: List<CategoryDose>,
    totalDose: Double,
    modifier: Modifier = Modifier
) {
    val colors = SoundDoseTheme.colors

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val strokeWidth = 22.dp.toPx()
            val arcSize = Size(size.width, size.height)
            var startAngle = -90f

            for (item in data) {
                val cat = try {
                    NoiseCategory.valueOf(item.category)
                } catch (e: Exception) {
                    NoiseCategory.AMBIENT
                }
                val sweep = ((item.totalDose / totalDose) * 360f).toFloat().coerceAtLeast(3f)

                drawArc(
                    color = Color(cat.colorHex),
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    size = arcSize
                )
                startAngle += sweep
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${"%.0f".format(totalDose)}%",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = colors.textPrimary
            )
            Text(
                text = "DAILY DOSE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
fun CategoryVectorRow(
    category: NoiseCategory,
    percent: Double
) {
    val colors = SoundDoseTheme.colors
    val categoryColor = Color(category.colorHex)
    val icon = CategoryIconHelper.getCategoryIcon(category)

    SmudgedCard(
        cornerRadius = 16.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(categoryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = category.label,
                    tint = categoryColor,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = category.label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "${"%.1f".format(percent)}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = categoryColor
                    )
                }

                Spacer(modifier = Modifier.height(7.dp))

                LinearProgressIndicator(
                    progress = { (percent / 100.0).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = categoryColor,
                    trackColor = if (colors.isDark) Color(0x18FFFFFF) else Color(0x2278716C)
                )
            }
        }
    }
}
