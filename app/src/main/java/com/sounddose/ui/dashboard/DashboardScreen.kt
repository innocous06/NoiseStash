package com.sounddose.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sounddose.audio.AudioPreset
import com.sounddose.audio.LiveState
import com.sounddose.dose.NoiseCategory
import com.sounddose.dose.SafetyStatus
import com.sounddose.ui.components.AmbientAudioBackground
import com.sounddose.ui.components.CategoryIconHelper
import com.sounddose.ui.components.SmudgedCard
import com.sounddose.ui.theme.*

@Composable
fun DashboardScreen(
    liveState: LiveState,
    onStartStop: () -> Unit,
    onSelectPreset: (AudioPreset) -> Unit,
    onAdjustCalibration: (Double) -> Unit,
    onOpenSettings: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val colors = SoundDoseTheme.colors

    Box(modifier = Modifier.fillMaxSize()) {
        // Layer 0: Ambient Background with Generative Harmonic Line Art & Smoothed Bars
        AmbientAudioBackground(
            waveformPoints = liveState.waveformPoints,
            currentDb = liveState.currentDb,
            isListening = liveState.isListening
        )

        // Layer 1: Foreground Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            // Top App Bar: NoiseStash (No tagline as requested) + Settings & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NoiseStash",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    color = colors.textPrimary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Clean Status Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (liveState.isListening) DoseSafe.copy(alpha = 0.14f) else colors.cardBackground)
                            .padding(horizontal = 11.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (liveState.isListening) DoseSafe else colors.textTertiary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (liveState.isListening) "ACTIVE" else "STANDBY",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = if (liveState.isListening) DoseSafe else colors.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Settings Button on Top Right (Point 3)
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(colors.cardBackgroundElevated)
                            .clickable { onOpenSettings() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = colors.textPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Heading for Demo Scenarios (Point 6)
            Text(
                text = "DEMO SCENARIOS & ACOUSTIC PRESETS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = colors.textSecondary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Capsule Sound Source Selector Bar (Matching reference image)
            AudioPresetSelector(
                activePreset = liveState.activePreset,
                onSelect = onSelectPreset
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Large Minimalist Decibel Meter with Peak and Average
            LiveMeter(
                currentDb = liveState.currentDb,
                peakDb = liveState.peakDb,
                sessionAvgDb = liveState.sessionAvgDb,
                isListening = liveState.isListening
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Acoustic Classification Chip (Smudged into canvas)
            SourceChip(
                category = liveState.currentCategory,
                rawLabel = liveState.rawLabel,
                confidence = liveState.confidence,
                isListening = liveState.isListening
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Centerpiece Hero Dose Ring with Dynamic Glow
            DoseRing(
                dosePercent = liveState.totalDosePercent,
                safeTimeRemainingHours = liveState.safeTimeRemainingHours,
                budgetMultiplier = liveState.budgetMultiplier
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Actionable Consumer Data Byproducts: 2 Smudged Cards
            ConsumerDataByproducts(liveState = liveState)

            Spacer(modifier = Modifier.height(18.dp))

            // Primary Action CTA Button (Capsule design)
            Button(
                onClick = onStartStop,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (liveState.isListening) DoseDanger else if (colors.isDark) Color(0xFF1E293B) else colors.pillActiveBg
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (liveState.isListening) Icons.Outlined.MicOff else Icons.Outlined.Mic,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (liveState.isListening) "PAUSE MONITORING" else "START DOSE MONITORING",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}

@Composable
fun AudioPresetSelector(
    activePreset: AudioPreset,
    onSelect: (AudioPreset) -> Unit
) {
    val presets = AudioPreset.values()
    val scroll = rememberScrollState()
    val colors = SoundDoseTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scroll),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        presets.forEach { preset ->
            val isSelected = activePreset == preset
            val pillShape = RoundedCornerShape(50) // Pure stadium capsule matching uploaded reference

            Box(
                modifier = Modifier
                    .clip(pillShape)
                    .then(
                        if (isSelected) {
                            Modifier.background(colors.pillActiveBg)
                        } else {
                            Modifier
                                .background(Color.Transparent)
                                .border(1.dp, colors.pillInactiveBorder, pillShape)
                        }
                    )
                    .clickable { onSelect(preset) }
                    .padding(horizontal = 18.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (preset == AudioPreset.REAL_MIC) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) (if (colors.isDark) Color(0xFF090D16) else Color(0xFFFEF9E7))
                                    else DoseSafe
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = preset.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        letterSpacing = 0.2.sp,
                        color = if (isSelected) colors.pillActiveText else colors.pillInactiveText
                    )
                }
            }
        }
    }
}

@Composable
fun SourceChip(
    category: NoiseCategory,
    rawLabel: String,
    confidence: Float,
    isListening: Boolean
) {
    val colors = SoundDoseTheme.colors
    val categoryColor = Color(category.colorHex)
    val icon = CategoryIconHelper.getCategoryIcon(category)

    SmudgedCard(
        cornerRadius = 16.dp,
        accentGlow = if (isListening) categoryColor else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(categoryColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isListening) category.label else "Acoustic Standby",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                if (isListening) {
                    Text(
                        text = "Identified: $rawLabel (${(confidence * 100).toInt()}% match)",
                        fontSize = 10.sp,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun ConsumerDataByproducts(liveState: LiveState) {
    val colors = SoundDoseTheme.colors

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Card 1: Inner-Ear Recovery Rest
        SmudgedCard(
            cornerRadius = 16.dp,
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Bedtime,
                        contentDescription = null,
                        tint = DoseCaution,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ear Recovery",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (liveState.earRecoveryHours > 0) "${liveState.earRecoveryHours}h quiet rest" else "Fully rested",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = colors.textPrimary
                )
                Text(
                    text = "in ambient <65 dB",
                    fontSize = 9.sp,
                    color = colors.textTertiary
                )
            }
        }

        // Card 2: Hardware SPL Engine (Image 4 technique: Raw acoustic ingestion bypassing AGC, <100ms window)
        SmudgedCard(
            cornerRadius = 16.dp,
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = DoseSafe,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "True SPL Engine",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Raw Audio (No AGC)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = DoseSafe
                )
                Text(
                    text = "<100ms peak window • Offline",
                    fontSize = 9.sp,
                    color = colors.textTertiary
                )
            }
        }
    }
}


