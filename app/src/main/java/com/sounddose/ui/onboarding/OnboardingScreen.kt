package com.sounddose.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sounddose.data.UserManager
import com.sounddose.ui.components.AmbientAudioBackground
import com.sounddose.ui.components.SmudgedCard
import com.sounddose.ui.theme.DoseSafe
import com.sounddose.ui.theme.SoundDoseTheme

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userManager = remember { UserManager.getInstance(context) }
    val colors = SoundDoseTheme.colors

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf(24f) }
    var selectedEnvironment by remember { mutableStateOf(UserManager.ENVIRONMENTS[0]) }
    var selectedProfession by remember { mutableStateOf(UserManager.PROFESSIONS[3]) }

    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        AmbientAudioBackground(
            waveformPoints = FloatArray(24) { 0.05f },
            currentDb = 40.0,
            isListening = false
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // App Brand Emblem
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(colors.visualizerColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.GraphicEq,
                    contentDescription = null,
                    tint = colors.visualizerColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome to NoiseStash",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                color = colors.textPrimary
            )
            Text(
                text = "Personalize your hearing conservation profile",
                fontSize = 13.sp,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 1. Name Input
            SmudgedCard(
                cornerRadius = 18.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "YOUR NAME",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("e.g. Alex", color = colors.textTertiary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.visualizerColor,
                            unfocusedBorderColor = colors.cardBorder,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Age Slider
            SmudgedCard(
                cornerRadius = 18.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AGE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = colors.textSecondary
                        )
                        Text(
                            text = "${age.toInt()} years",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.visualizerColor
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = age,
                        onValueChange = { age = it },
                        valueRange = 14f..80f,
                        colors = SliderDefaults.colors(
                            thumbColor = colors.visualizerColor,
                            activeTrackColor = colors.visualizerColor,
                            inactiveTrackColor = colors.cardBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Section 1: DAILY ACOUSTIC ENVIRONMENT (Independent Section)
            SmudgedCard(
                cornerRadius = 18.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Commute,
                            contentDescription = null,
                            tint = colors.visualizerColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DAILY ACOUSTIC ENVIRONMENT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = colors.textSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    val envScroll = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(envScroll),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UserManager.ENVIRONMENTS.forEach { env ->
                            val isSelected = selectedEnvironment == env
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
                                    .clickable { selectedEnvironment = env }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = env,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) colors.pillActiveText else colors.pillInactiveText
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Section 2: PRIMARY OCCUPATION / LIFESTYLE (Independent Section)
            SmudgedCard(
                cornerRadius = 18.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.WorkOutline,
                            contentDescription = null,
                            tint = colors.visualizerColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PRIMARY OCCUPATION / LIFESTYLE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = colors.textSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

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
                                    .clickable { selectedProfession = prof }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
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

            Spacer(modifier = Modifier.height(28.dp))

            // CTA Button
            Button(
                onClick = {
                    val finalName = name.ifBlank { "You" }
                    userManager.saveUserProfile(
                        name = finalName,
                        age = age.toInt(),
                        profession = selectedProfession,
                        environment = selectedEnvironment
                    )
                    userManager.setOnboarded(true)
                    onComplete()
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.pillActiveBg
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = "ENTER NOISESTASH",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = colors.pillActiveText
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
