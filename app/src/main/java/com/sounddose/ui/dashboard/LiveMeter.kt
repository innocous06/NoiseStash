package com.sounddose.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sounddose.ui.theme.*

@Composable
fun LiveMeter(
    currentDb: Double,
    peakDb: Double,
    sessionAvgDb: Double,
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedDb by animateFloatAsState(
        targetValue = if (isListening) currentDb.toFloat() else 34f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 450f),
        label = "db_value"
    )

    val dbColor = getDoseColor((currentDb - 45.0).coerceIn(0.0, 100.0))

    val colors = SoundDoseTheme.colors

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "${animatedDb.toInt()}",
                fontSize = 82.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-3).sp,
                color = colors.textPrimary
            )
            Column(
                modifier = Modifier.padding(bottom = 18.dp, start = 8.dp)
            ) {
                Text(
                    text = "dB SPL",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = if (isListening) dbColor else colors.textSecondary
                )
            }
        }

        // Sub-telemetry: Peak and Average
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PEAK ${peakDb.toInt()} dB",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = colors.textSecondary
            )
            Text(
                text = "•",
                fontSize = 10.sp,
                color = colors.textTertiary
            )
            Text(
                text = "AVG ${sessionAvgDb.toInt()} dB",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = colors.textSecondary
            )
        }
    }
}
