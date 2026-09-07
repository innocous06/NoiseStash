package com.sounddose.ui.circle

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sounddose.data.UserManager
import com.sounddose.ui.components.AmbientAudioBackground
import com.sounddose.ui.components.SmudgedCard
import com.sounddose.ui.theme.*

data class FriendMetric(
    val rank: Int,
    val name: String,
    val doseUsedPercent: Double,
    val bankSavedPercent: Double,
    val streakDays: Int,
    val badge: String,
    val isUser: Boolean = false
)

@Composable
fun FriendsCircleScreen(
    userDosePercent: Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userManager = remember { UserManager.getInstance(context) }
    val userProfile = remember { userManager.getUserProfile() }
    val colors = SoundDoseTheme.colors
    val scrollState = rememberScrollState()

    val userSaved = (100.0 - userDosePercent).coerceIn(0.0, 100.0)

    val userDisplayName = remember(userProfile.name) {
        val raw = userProfile.name.trim()
        if (raw.equals("You", ignoreCase = true) || raw.isEmpty()) "You" else "$raw (You)"
    }

    val friendsList = remember(userDosePercent, userDisplayName) {
        listOf(
            FriendMetric(1, "Sarah K.", 18.0, 82.0, 7, "Gold Shield"),
            FriendMetric(2, userDisplayName, userDosePercent.coerceIn(0.0, 100.0), userSaved, 5, "Gold Shield", isUser = true),
            FriendMetric(3, "Marcus T.", 48.0, 52.0, 4, "Silver Guard"),
            FriendMetric(4, "Priya M.", 76.0, 24.0, 2, "Bronze Ear"),
            FriendMetric(5, "David L.", 94.0, 6.0, 0, "Risk Alert")
        ).sortedBy { it.doseUsedPercent }.mapIndexed { idx, it -> it.copy(rank = idx + 1) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AmbientAudioBackground(
            waveformPoints = FloatArray(24) { 0.05f },
            currentDb = 42.0,
            isListening = false
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Friends Circle",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    color = colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero League Status Card
            SmudgedCard(
                cornerRadius = 24.dp,
                accentGlow = DoseSafe,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(DoseSafe.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.MilitaryTech,
                                    contentDescription = null,
                                    tint = DoseSafe,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "HEARING PRESERVATION LEAGUE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.6.sp,
                                    maxLines = 1,
                                    softWrap = false,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = "Rank #2 • Gold Guardian",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false,
                                    color = colors.textPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(colors.cardBackgroundElevated)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "5-Day Streak 🔥",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                color = colors.visualizerColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "Hearing Bank Saved Today",
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                            Text(
                                text = "${"%.0f".format(userSaved)}%",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = DoseSafe
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Daily Budget Used",
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                            Text(
                                text = "${"%.1f".format(userDosePercent)}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = getDoseColor(userDosePercent)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Leaderboard Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "COMMUNITY CONSERVATION RANKING",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp,
                    maxLines = 1,
                    softWrap = false,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Lowest Dose Wins",
                    fontSize = 10.sp,
                    maxLines = 1,
                    softWrap = false,
                    color = colors.textTertiary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Friends Leaderboard List
            friendsList.forEach { friend ->
                FriendLeaderboardCard(
                    friend = friend,
                    onNudge = {
                        Toast.makeText(
                            context,
                            "Sent ear rest reminder nudge to ${friend.name}!",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onCheer = {
                        Toast.makeText(
                            context,
                            "Sent cheers to ${friend.name}! 🌟",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Hearing Challenges Section
            SmudgedCard(
                cornerRadius = 20.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.WorkspacePremium,
                            contentDescription = null,
                            tint = colors.visualizerColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Daily Protective Challenge",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cap today's exposure under 60% to earn +50 League Points and protect your high-frequency auditory cells.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FriendLeaderboardCard(
    friend: FriendMetric,
    onNudge: () -> Unit,
    onCheer: () -> Unit
) {
    val colors = SoundDoseTheme.colors
    val doseColor = getDoseColor(friend.doseUsedPercent)

    SmudgedCard(
        cornerRadius = 16.dp,
        accentGlow = if (friend.isUser) colors.visualizerColor else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number
            Text(
                text = "#${friend.rank}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = if (friend.rank == 1) colors.visualizerColor else colors.textSecondary,
                modifier = Modifier.width(28.dp)
            )

            // Avatar circle
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (friend.isUser) colors.visualizerColor.copy(alpha = 0.22f) else colors.cardBackgroundElevated),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = friend.name.take(1),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (friend.isUser) colors.visualizerColor else colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Name & details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.name,
                    fontSize = 13.sp,
                    fontWeight = if (friend.isUser) FontWeight.Black else FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = "Saved ${friend.bankSavedPercent.toInt()}% of hearing bank",
                    fontSize = 11.sp,
                    color = colors.textSecondary
                )
            }

            // Dose pill and action button
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(doseColor.copy(alpha = 0.16f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "${friend.doseUsedPercent.toInt()}% dose",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = doseColor
                    )
                }

                if (!friend.isUser) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { if (friend.doseUsedPercent >= 80) onNudge() else onCheer() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (friend.doseUsedPercent >= 80) Icons.Outlined.NotificationsActive else Icons.Outlined.ThumbUp,
                            contentDescription = "Interact",
                            tint = if (friend.doseUsedPercent >= 80) DoseDanger else colors.visualizerColor,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}
