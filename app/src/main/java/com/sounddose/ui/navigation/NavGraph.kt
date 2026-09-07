package com.sounddose.ui.navigation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sounddose.audio.AudioPreset
import com.sounddose.audio.LiveState
import com.sounddose.data.CategoryDose
import com.sounddose.data.DailyDoseSummary
import com.sounddose.ui.circle.FriendsCircleScreen
import com.sounddose.ui.dashboard.DashboardScreen
import com.sounddose.ui.stats.StatisticsScreen
import com.sounddose.ui.theme.SoundDoseTheme
import kotlinx.coroutines.launch

sealed class NavTab(val title: String, val icon: ImageVector) {
    object Home : NavTab("Home", Icons.Outlined.Home)
    object Statistics : NavTab("Statistics", Icons.AutoMirrored.Outlined.ShowChart)
    object Circle : NavTab("Circle", Icons.Outlined.Group)
}

@Composable
fun SoundDoseNavHost(
    liveState: LiveState,
    categoryBreakdown: List<CategoryDose>,
    dailySummaries: List<DailyDoseSummary>,
    onStartStop: () -> Unit,
    onSelectPreset: (AudioPreset) -> Unit,
    onAdjustCalibration: (Double) -> Unit,
    onOpenSettings: () -> Unit = {}
) {
    val tabs = remember { listOf(NavTab.Home, NavTab.Statistics, NavTab.Circle) }
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
    val coroutineScope = rememberCoroutineScope()
    val colors = SoundDoseTheme.colors

    Scaffold(
        containerColor = colors.background,
        bottomBar = {
            // Sleek Minimalist Outline Bottom Bar (Matching user reference)
            NavigationBar(
                containerColor = colors.bottomBarBg,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = colors.bottomBarBorder, shape = RectangleShape)
            ) {
                tabs.forEachIndexed { index, tab ->
                    val selected = pagerState.currentPage == index
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (pagerState.currentPage != index) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = colors.bottomBarActive,
                            selectedTextColor = colors.bottomBarActive,
                            unselectedIconColor = colors.bottomBarInactive,
                            unselectedTextColor = colors.bottomBarInactive,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        // Swipeable Tabs via HorizontalPager (Point 2: switching between tabs by swiping left and right)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { pageIndex ->
            when (pageIndex) {
                0 -> DashboardScreen(
                    liveState = liveState,
                    onStartStop = onStartStop,
                    onSelectPreset = onSelectPreset,
                    onAdjustCalibration = onAdjustCalibration,
                    onOpenSettings = onOpenSettings
                )
                1 -> StatisticsScreen(
                    categoryBreakdown = categoryBreakdown,
                    dailySummaries = dailySummaries,
                    totalDose = liveState.totalDosePercent
                )
                2 -> FriendsCircleScreen(
                    userDosePercent = liveState.totalDosePercent
                )
            }
        }
    }
}
