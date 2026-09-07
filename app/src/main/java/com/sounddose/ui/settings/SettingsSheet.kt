package com.sounddose.ui.settings

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sounddose.data.UserManager
import com.sounddose.ui.components.SmudgedCard
import com.sounddose.ui.theme.DoseSafe
import com.sounddose.ui.theme.LocalThemeToggle
import com.sounddose.ui.theme.SoundDoseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    currentOffset: Double,
    onAdjustCalibration: (Double) -> Unit,
    onUpdateBudgetMultiplier: ((Float) -> Unit)? = null,
    onDismiss: () -> Unit,
    onReplaySplash: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userManager = remember { UserManager.getInstance(context) }
    val colors = SoundDoseTheme.colors
    val onToggleTheme = LocalThemeToggle.current

    var profile by remember { mutableStateOf(userManager.getUserProfile()) }
    var name by remember { mutableStateOf(profile.name) }
    var age by remember { mutableStateOf(profile.age.toFloat()) }
    var selectedProfession by remember { mutableStateOf(profile.profession) }
    var budgetMultiplier by remember { mutableStateOf(profile.budgetMultiplier) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.backgroundElevated,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 8.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colors.cardBorder)
            )
        }
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .verticalScroll(scrollState)
        ) {
            // Title Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Settings & Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Appearance / Theme Toggle
            SmudgedCard(cornerRadius = 16.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (colors.isDark) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                            contentDescription = null,
                            tint = colors.visualizerColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Theme Appearance",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = if (colors.isDark) "Charcoal & Sleek Silver" else "Light Yellow Amber & Orange",
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(colors.pillActiveBg)
                            .clickable { onToggleTheme() }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (colors.isDark) "LIGHT" else "DARK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.pillActiveText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Microphone Decibel Trimmer
            SmudgedCard(cornerRadius = 16.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = null,
                                tint = DoseSafe,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Microphone Decibel Trimmer",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }
                        Text(
                            text = "${if (currentOffset >= 0) "+" else ""}${"%.0f".format(currentOffset)} dB",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.visualizerColor
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(-2.0, -1.0, 1.0, 2.0).forEach { delta ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(colors.cardBackgroundElevated)
                                    .clickable { onAdjustCalibration(delta) }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = "${if (delta > 0) "+" else ""}${"%.0f".format(delta)} dB",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Safe Hearing Budget Multiplier
            SmudgedCard(cornerRadius = 16.dp) {
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
                                tint = colors.visualizerColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Daily Safe Budget Ceiling",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }
                        Text(
                            text = "${(budgetMultiplier * 100).toInt()}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.visualizerColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Customize the daily noise dose allowance (lower for protective sensitivity).",
                        fontSize = 10.sp,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Slider(
                        value = budgetMultiplier,
                        onValueChange = {
                            budgetMultiplier = it
                            userManager.setBudgetMultiplier(it)
                            onUpdateBudgetMultiplier?.invoke(it)
                        },
                        valueRange = 0.70f..1.30f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = colors.visualizerColor,
                            activeTrackColor = colors.visualizerColor,
                            inactiveTrackColor = colors.cardBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. User Profile Settings (Name, Age, Profession)
            SmudgedCard(cornerRadius = 16.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "AUDIOLOGY PROFILE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            userManager.saveUserProfile(it, age.toInt(), selectedProfession, budgetMultiplier)
                        },
                        label = { Text("Name", color = colors.textSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.visualizerColor,
                            unfocusedBorderColor = colors.cardBorder,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Age
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Age: ${age.toInt()} years", fontSize = 12.sp, color = colors.textPrimary)
                    }
                    Slider(
                        value = age,
                        onValueChange = {
                            age = it
                            userManager.saveUserProfile(name, it.toInt(), selectedProfession, budgetMultiplier)
                        },
                        valueRange = 14f..80f,
                        colors = SliderDefaults.colors(
                            thumbColor = colors.visualizerColor,
                            activeTrackColor = colors.visualizerColor,
                            inactiveTrackColor = colors.cardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Profession Pills
                    Text(text = "Profession:", fontSize = 11.sp, color = colors.textSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    val profScroll = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(profScroll),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UserManager.PROFESSIONS.forEach { prof ->
                            val isSelected = selectedProfession == prof
                            val shape = RoundedCornerShape(50)
                            Box(
                                modifier = Modifier
                                    .clip(shape)
                                    .then(
                                        if (isSelected) {
                                            Modifier.background(colors.pillActiveBg)
                                        } else {
                                            Modifier
                                                .background(Color.Transparent)
                                                .border(1.dp, colors.pillInactiveBorder, shape)
                                        }
                                    )
                                    .clickable {
                                        selectedProfession = prof
                                        userManager.saveUserProfile(name, age.toInt(), prof, budgetMultiplier)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = prof,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) colors.pillActiveText else colors.pillInactiveText
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Replay Opening Animation Option
            SmudgedCard(
                cornerRadius = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        onReplaySplash()
                    }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayCircleOutline,
                        contentDescription = null,
                        tint = colors.visualizerColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Replay Opening Animation",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Watch the generative particle logo animation again",
                            fontSize = 10.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
