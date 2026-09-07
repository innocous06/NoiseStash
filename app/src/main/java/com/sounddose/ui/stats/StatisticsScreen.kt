package com.sounddose.ui.stats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sounddose.data.CategoryDose
import com.sounddose.data.DailyDoseSummary
import com.sounddose.dose.NoiseCategory
import com.sounddose.ui.components.AmbientAudioBackground
import com.sounddose.ui.components.CategoryIconHelper
import com.sounddose.ui.components.SmudgedCard
import com.sounddose.ui.theme.*

enum class StatsTimeframe(val label: String) {
    TODAY("Today"),
    LAST_WEEK("Last Week"),
    THIS_MONTH("This Month")
}

data class GpsHotspot(
    val placeName: String,
    val coordinates: String,
    val soundType: String,
    val limitPercent: Double,
    val avgDb: Double,
    val category: NoiseCategory
)

data class HourlyDose(
    val hour: Int,
    val percentOfLimit: Double,
    val avgDb: Double
)

data class DecompositionSource(
    val label: String,
    val shortLabel: String,
    val percentage: Int,
    val color: Color,
    val description: String = ""
)

data class MonthDayData(
    val dayOfMonth: Int,
    val dayName: String,
    val dateLabel: String,
    val totalDosePercent: Double,
    val avgDb: Double,
    val peakDb: Double,
    val topSource: String,
    val hourlyData: List<HourlyDose>,
    val categories: List<CategoryDose>,
    val sources: List<DecompositionSource> = emptyList()
)

data class WeekDayData(
    val dayOfWeek: String,
    val dayNumber: String,
    val fullDate: String,
    val totalDose: Double,
    val hourlyData: List<HourlyDose>,
    val sources: List<DecompositionSource> = emptyList()
)

@Composable
fun StatisticsScreen(
    categoryBreakdown: List<CategoryDose>,
    dailySummaries: List<DailyDoseSummary>,
    totalDose: Double,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val colors = SoundDoseTheme.colors

    var selectedTimeframe by remember { mutableStateOf(StatsTimeframe.TODAY) }
    var selectedHourIndex by remember { mutableStateOf(14) }
    var selectedWeekDayIndex by remember { mutableStateOf(4) }
    var selectedMonthDayIndex by remember { mutableStateOf(6) }

    val todayHourlyData = remember {
        listOf(
            HourlyDose(0, 1.0, 42.0),
            HourlyDose(1, 0.5, 38.0),
            HourlyDose(2, 0.5, 36.0),
            HourlyDose(3, 0.5, 37.0),
            HourlyDose(4, 1.2, 44.0),
            HourlyDose(5, 2.0, 49.0),
            HourlyDose(6, 4.5, 58.0),
            HourlyDose(7, 12.0, 78.0),
            HourlyDose(8, 26.0, 89.0),
            HourlyDose(9, 18.0, 84.0),
            HourlyDose(10, 8.0, 72.0),
            HourlyDose(11, 6.0, 68.0),
            HourlyDose(12, 14.0, 79.0),
            HourlyDose(13, 9.0, 73.0),
            HourlyDose(14, 34.0, 92.0),
            HourlyDose(15, 12.0, 77.0),
            HourlyDose(16, 10.0, 74.0),
            HourlyDose(17, 28.0, 90.0),
            HourlyDose(18, 31.0, 91.0),
            HourlyDose(19, 19.0, 82.0),
            HourlyDose(20, 11.0, 76.0),
            HourlyDose(21, 6.0, 65.0),
            HourlyDose(22, 3.0, 52.0),
            HourlyDose(23, 1.5, 45.0)
        )
    }

    val hotspots = remember {
        listOf(
            GpsHotspot(
                placeName = "Central Metro Hub & Crossroad",
                coordinates = "12.9716° N, 77.5946° E",
                soundType = "Horns & Diesel Traffic",
                limitPercent = 44.0,
                avgDb = 94.0,
                category = NoiseCategory.HORN
            ),
            GpsHotspot(
                placeName = "Highway Elevated Flyover",
                coordinates = "12.9352° N, 77.6245° E",
                soundType = "Engine Drone & Speed Accel",
                limitPercent = 28.0,
                avgDb = 89.0,
                category = NoiseCategory.TRAFFIC
            ),
            GpsHotspot(
                placeName = "Commercial Crossing Metro Ext",
                coordinates = "12.9801° N, 77.5812° E",
                soundType = "Jackhammers & Foundation Drill",
                limitPercent = 16.0,
                avgDb = 97.0,
                category = NoiseCategory.CONSTRUCTION
            ),
            GpsHotspot(
                placeName = "Campus Library & Co-work",
                coordinates = "12.9611° N, 77.6387° E",
                soundType = "Ambient Chatter & HVAC",
                limitPercent = 4.0,
                avgDb = 62.0,
                category = NoiseCategory.AMBIENT
            )
        )
    }

    // Unique Predefined Source Decomposition for Today
    val todaySources = remember {
        listOf(
            DecompositionSource("VEHICLE HORNS", "HORNS", 44, Color(0xFFEF4444), "Buses, auto-rickshaws & two-wheelers"),
            DecompositionSource("TRAFFIC CORRIDOR", "TRAFFIC", 32, Color(0xFFF59E0B), "Accelerating combustion engines"),
            DecompositionSource("MACHINERY & DRILLS", "DRILLS", 16, Color(0xFF64748B), "Flyover piling & pneumatic drills"),
            DecompositionSource("AMBIENT & CHATTER", "AMBIENT", 8, Color(0xFF10B981), "Urban crowd & wind turbulence")
        )
    }

    // 7-Day Week Data with realistic human daily doses & clean labels (never crumbling)
    val weekDays = remember {
        listOf(
            WeekDayData(
                dayOfWeek = "Mon",
                dayNumber = "02",
                fullDate = "Sep 02, 2026",
                totalDose = 78.0,
                hourlyData = todayHourlyData.map { it.copy(percentOfLimit = (it.percentOfLimit * 0.85).coerceAtLeast(0.5)) },
                sources = listOf(
                    DecompositionSource("TRAFFIC CORRIDOR", "TRAFFIC", 46, Color(0xFFF59E0B), "Morning commute arterial crawl"),
                    DecompositionSource("VEHICLE HORNS", "HORNS", 31, Color(0xFFEF4444), "Signal rush honking"),
                    DecompositionSource("METRO & COMMUTE", "TRANSIT", 14, Color(0xFF3B82F6), "Electric rail & subway whine"),
                    DecompositionSource("AMBIENT INDOOR", "AMBIENT", 9, Color(0xFF10B981), "Indoor office acoustic floor")
                )
            ),
            WeekDayData(
                dayOfWeek = "Tue",
                dayNumber = "03",
                fullDate = "Sep 03, 2026",
                totalDose = 64.0,
                hourlyData = todayHourlyData.map { it.copy(percentOfLimit = (it.percentOfLimit * 0.70).coerceAtLeast(0.5)) },
                sources = listOf(
                    DecompositionSource("VEHICLE HORNS", "HORNS", 42, Color(0xFFEF4444), "Commercial street passing blasts"),
                    DecompositionSource("TRAFFIC CORRIDOR", "TRAFFIC", 34, Color(0xFFF59E0B), "Main highway lane drone"),
                    DecompositionSource("CONSTRUCTION SITE", "DRILLS", 16, Color(0xFF64748B), "Pavement breaker near flyover"),
                    DecompositionSource("AMBIENT & CHATTER", "AMBIENT", 8, Color(0xFF10B981), "Quiet cafe & outdoor plaza")
                )
            ),
            WeekDayData(
                dayOfWeek = "Wed",
                dayNumber = "04",
                fullDate = "Sep 04, 2026",
                totalDose = 92.0,
                hourlyData = todayHourlyData,
                sources = listOf(
                    DecompositionSource("VEHICLE HORNS", "HORNS", 50, Color(0xFFEF4444), "Midweek gridlock horn clusters"),
                    DecompositionSource("TRAFFIC CORRIDOR", "TRAFFIC", 28, Color(0xFFF59E0B), "Heavy truck & bus engine revs"),
                    DecompositionSource("MACHINERY & DRILLS", "DRILLS", 14, Color(0xFF64748B), "Metro line foundation drilling"),
                    DecompositionSource("HEADPHONE AUDIO", "AUDIO", 8, Color(0xFF8B5CF6), "High-volume podcast stream")
                )
            ),
            WeekDayData(
                dayOfWeek = "Thu",
                dayNumber = "05",
                fullDate = "Sep 05, 2026",
                totalDose = 58.0,
                hourlyData = todayHourlyData.map { it.copy(percentOfLimit = (it.percentOfLimit * 0.65).coerceAtLeast(0.5)) },
                sources = listOf(
                    DecompositionSource("CONSTRUCTION & SITE", "DRILLS", 45, Color(0xFF64748B), "Concrete drilling & heavy machinery"),
                    DecompositionSource("TRAFFIC CORRIDOR", "TRAFFIC", 28, Color(0xFFF59E0B), "Service road vehicle movement"),
                    DecompositionSource("VEHICLE HORNS", "HORNS", 18, Color(0xFFEF4444), "Narrow turn alert horns"),
                    DecompositionSource("AMBIENT & OFFICE", "AMBIENT", 9, Color(0xFF10B981), "AC vents & indoor quiet")
                )
            ),
            WeekDayData(
                dayOfWeek = "Fri",
                dayNumber = "06",
                fullDate = "Sep 06, 2026",
                totalDose = 104.0,
                hourlyData = todayHourlyData.map { it.copy(percentOfLimit = (it.percentOfLimit * 1.15).coerceAtLeast(0.5)) },
                sources = listOf(
                    DecompositionSource("TRAFFIC CORRIDOR", "TRAFFIC", 38, Color(0xFFF59E0B), "Weekend departure highway traffic"),
                    DecompositionSource("VEHICLE HORNS", "HORNS", 32, Color(0xFFEF4444), "Friday evening rush hour jams"),
                    DecompositionSource("SOCIAL & VENUES", "VENUES", 21, Color(0xFFEC4899), "Restaurant & evening lounge audio"),
                    DecompositionSource("AMBIENT", "AMBIENT", 9, Color(0xFF10B981), "Pedestrian mall ambiance")
                )
            ),
            WeekDayData(
                dayOfWeek = "Sat",
                dayNumber = "07",
                fullDate = "Sep 07, 2026",
                totalDose = 32.0,
                hourlyData = todayHourlyData.map { it.copy(percentOfLimit = (it.percentOfLimit * 0.35).coerceAtLeast(0.3)) },
                sources = listOf(
                    DecompositionSource("HEADPHONE AUDIO", "AUDIO", 46, Color(0xFF8B5CF6), "Music streaming & gaming sessions"),
                    DecompositionSource("SOCIAL & VENUES", "VENUES", 26, Color(0xFFEC4899), "Shopping center & cafes"),
                    DecompositionSource("ROAD TRAFFIC", "TRAFFIC", 18, Color(0xFFF59E0B), "City transit & cab rides"),
                    DecompositionSource("VEHICLE HORNS", "HORNS", 10, Color(0xFFEF4444), "Occasional vehicle honking")
                )
            ),
            WeekDayData(
                dayOfWeek = "Sun",
                dayNumber = "08",
                fullDate = "Sep 08, 2026",
                totalDose = 45.0,
                hourlyData = todayHourlyData.map { it.copy(percentOfLimit = (it.percentOfLimit * 0.50).coerceAtLeast(0.4)) },
                sources = listOf(
                    DecompositionSource("AMBIENT & HOME", "AMBIENT", 48, Color(0xFF10B981), "Quiet home, neighborhood & park"),
                    DecompositionSource("HEADPHONE AUDIO", "AUDIO", 28, Color(0xFF8B5CF6), "Relaxing podcasts & voice calls"),
                    DecompositionSource("ROAD TRAFFIC", "TRAFFIC", 16, Color(0xFFF59E0B), "Light Sunday afternoon driving"),
                    DecompositionSource("VEHICLE HORNS", "HORNS", 8, Color(0xFFEF4444), "Distant passing horns")
                )
            )
        )
    }

    // 30 Days of realistic, believable calendar data for the monthly view
    val monthDays = remember {
        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val doses = listOf(
            68.0, 74.0, 88.0, 92.0, 105.0, 36.0, 42.0,
            62.0, 78.0, 84.0, 89.0, 102.0, 31.0, 45.0,
            70.0, 76.0, 82.0, 95.0, 108.0, 40.0, 48.0,
            65.0, 72.0, 86.0, 90.0, 104.0, 34.0, 44.0,
            71.0, 79.0
        )
        (1..30).map { day ->
            val dayName = daysOfWeek[(day - 1) % 7]
            val dose = doses[day - 1]
            val dateLabel = "Sep %02d".format(day)
            val topSource = when {
                day % 7 == 6 -> "Headphone Audio"
                day % 7 == 0 -> "Ambient & Home"
                day % 5 == 0 -> "Machinery & Drills"
                day % 4 == 0 -> "Vehicle Horns"
                day % 3 == 0 -> "Traffic Corridor"
                else -> "Traffic Corridor"
            }
            val hourly = todayHourlyData.map {
                val scale = dose / 92.0
                it.copy(percentOfLimit = (it.percentOfLimit * scale).coerceAtLeast(0.5))
            }
            val cats = listOf(
                CategoryDose("HORN", dose * 0.44),
                CategoryDose("TRAFFIC", dose * 0.32),
                CategoryDose("CONSTRUCTION", dose * 0.16),
                CategoryDose("AMBIENT", dose * 0.08)
            )
            val daySources = when {
                day % 7 == 6 -> listOf(
                    DecompositionSource("HEADPHONE AUDIO", "AUDIO", 46, Color(0xFF8B5CF6), "Personal media & streaming"),
                    DecompositionSource("SOCIAL & VENUES", "VENUES", 26, Color(0xFFEC4899), "Weekend market & cafes"),
                    DecompositionSource("URBAN TRAFFIC", "TRAFFIC", 18, Color(0xFFF59E0B), "Weekend transit ride"),
                    DecompositionSource("VEHICLE HORNS", "HORNS", 10, Color(0xFFEF4444), "City intersection honks")
                )
                day % 7 == 0 -> listOf(
                    DecompositionSource("AMBIENT & HOME", "AMBIENT", 50, Color(0xFF10B981), "Quiet resting environment"),
                    DecompositionSource("HEADPHONE AUDIO", "AUDIO", 26, Color(0xFF8B5CF6), "Low-volume relaxation music"),
                    DecompositionSource("ROAD TRAFFIC", "TRAFFIC", 16, Color(0xFFF59E0B), "Light suburban traffic"),
                    DecompositionSource("VEHICLE HORNS", "HORNS", 8, Color(0xFFEF4444), "Sparse distant horns")
                )
                day % 5 == 0 -> listOf(
                    DecompositionSource("MACHINERY & DRILLS", "DRILLS", 46, Color(0xFF64748B), "Infrastructure construction"),
                    DecompositionSource("TRAFFIC CORRIDOR", "TRAFFIC", 28, Color(0xFFF59E0B), "Heavy vehicle congestion"),
                    DecompositionSource("VEHICLE HORNS", "HORNS", 18, Color(0xFFEF4444), "Worksite bypass honking"),
                    DecompositionSource("AMBIENT", "AMBIENT", 8, Color(0xFF10B981), "Background environmental noise")
                )
                day % 4 == 0 -> listOf(
                    DecompositionSource("VEHICLE HORNS", "HORNS", 52, Color(0xFFEF4444), "Dense junction horn blasts"),
                    DecompositionSource("TRAFFIC CORRIDOR", "TRAFFIC", 26, Color(0xFFF59E0B), "Rush hour arterial vehicles"),
                    DecompositionSource("MACHINERY", "DRILLS", 13, Color(0xFF64748B), "Flyover maintenance tools"),
                    DecompositionSource("AMBIENT", "AMBIENT", 9, Color(0xFF10B981), "Open air dissipation")
                )
                day % 3 == 0 -> listOf(
                    DecompositionSource("TRAFFIC CORRIDOR", "TRAFFIC", 48, Color(0xFFF59E0B), "Highway vehicle acceleration"),
                    DecompositionSource("VEHICLE HORNS", "HORNS", 30, Color(0xFFEF4444), "Overtaking horn signals"),
                    DecompositionSource("METRO & COMMUTE", "TRANSIT", 13, Color(0xFF3B82F6), "Transit station noise"),
                    DecompositionSource("AMBIENT", "AMBIENT", 9, Color(0xFF10B981), "Station platform ambient")
                )
                else -> listOf(
                    DecompositionSource("VEHICLE HORNS", "HORNS", 41, Color(0xFFEF4444), "City road continuous horns"),
                    DecompositionSource("TRAFFIC CORRIDOR", "TRAFFIC", 33, Color(0xFFF59E0B), "Bus and commercial vehicles"),
                    DecompositionSource("CONSTRUCTION SITE", "DRILLS", 17, Color(0xFF64748B), "Site jackhammers and piling"),
                    DecompositionSource("AMBIENT", "AMBIENT", 9, Color(0xFF10B981), "Urban background noise")
                )
            }
            MonthDayData(
                dayOfMonth = day,
                dayName = dayName,
                dateLabel = dateLabel,
                totalDosePercent = dose,
                avgDb = 72.0 + (dose * 0.15),
                peakDb = 88.0 + (dose * 0.12),
                topSource = topSource,
                hourlyData = hourly,
                categories = cats,
                sources = daySources
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AmbientAudioBackground(
            waveformPoints = FloatArray(24) { 0.06f },
            currentDb = 45.0,
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Acoustic Statistics",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    color = colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TimeframeCapsuleSelector(
                selected = selectedTimeframe,
                onSelect = { selectedTimeframe = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            when (selectedTimeframe) {
                StatsTimeframe.TODAY -> {
                    // Apple Battery Style 24-Hour Cylinder Chart
                    SmudgedCard(
                        cornerRadius = 24.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "HOURLY EXPOSURE LOAD",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = "24-Hour Battery Timeline",
                                    fontSize = 11.sp,
                                    color = colors.visualizerColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            HourlyPillBarChart(
                                hourlyData = todayHourlyData,
                                selectedIndex = selectedHourIndex,
                                onSelectHour = { selectedHourIndex = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(190.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // GPS Hotspots
                    Text(
                        text = "HIGH EXPOSURE HOTSPOTS (GPS & SOUND)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = colors.textSecondary,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    hotspots.forEach { spot ->
                        GpsHotspotCard(spot = spot)
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Image 3 Inspiration: Classified Acoustic Waveform Stream ("What made it loud?")
                    ClassifiedAcousticStreamCard()

                    Spacer(modifier = Modifier.height(20.dp))

                    // Image 2 Inspiration: Clean Segmented Donut & Monospace Source Decomposition
                    SourceDecompositionCard(
                        sources = todaySources,
                        totalDose = if (totalDose > 0.5) totalDose else 92.0,
                        title = "TODAY • SOURCE DECOMPOSITION"
                    )
                }

                StatsTimeframe.LAST_WEEK -> {
                    // Robust 7-Day Limit Usage Bar Chart (Never crumbles on larger font/display sizes)
                    SmudgedCard(
                        cornerRadius = 24.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "7-DAY LIMIT USAGE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = "Tap bar to drill down",
                                    fontSize = 10.sp,
                                    color = colors.textTertiary
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            WeeklyInteractiveBarChart(
                                days = weekDays,
                                selectedIndex = selectedWeekDayIndex.coerceIn(0, weekDays.size - 1),
                                onSelectDay = { selectedWeekDayIndex = it },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    val activeDay = weekDays.getOrNull(selectedWeekDayIndex.coerceIn(0, weekDays.size - 1))
                    if (activeDay != null) {
                        Text(
                            text = "DRILLDOWN FOR ${activeDay.fullDate.uppercase()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = colors.visualizerColor,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        SmudgedCard(
                            cornerRadius = 20.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Hourly Breakdown (${activeDay.dayOfWeek} ${activeDay.dayNumber})",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = "${activeDay.totalDose.toInt()}% Dose",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = getDoseColor(activeDay.totalDose)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                HourlyPillBarChart(
                                    hourlyData = activeDay.hourlyData,
                                    selectedIndex = selectedHourIndex,
                                    onSelectHour = { selectedHourIndex = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(170.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        SourceDecompositionCard(
                            sources = activeDay.sources.ifEmpty { todaySources },
                            totalDose = activeDay.totalDose,
                            title = "${activeDay.dayOfWeek.uppercase()} • SOURCE DECOMPOSITION"
                        )
                    }
                }

                StatsTimeframe.THIS_MONTH -> {
                    // 30-Day Cumulative Timeline with interactive bar selection
                    SmudgedCard(
                        cornerRadius = 24.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "30-DAY CUMULATIVE LOAD",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = "Tap any day to inspect",
                                    fontSize = 10.sp,
                                    color = colors.visualizerColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            MonthlyTimelineChart(
                                days = monthDays,
                                selectedIndex = selectedMonthDayIndex.coerceIn(0, monthDays.size - 1),
                                onSelectDay = { selectedMonthDayIndex = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(175.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    val selectedMonthDay = monthDays.getOrNull(selectedMonthDayIndex.coerceIn(0, monthDays.size - 1))
                    if (selectedMonthDay != null) {
                        Text(
                            text = "ANALYSIS FOR ${selectedMonthDay.dateLabel.uppercase()}, 2026",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = colors.visualizerColor,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Selected Day Card with 24-Hour Battery Timeline
                        SmudgedCard(
                            cornerRadius = 20.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${selectedMonthDay.dayName}, ${selectedMonthDay.dateLabel}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textPrimary
                                        )
                                        Text(
                                            text = "Top hazard: ${selectedMonthDay.topSource}",
                                            fontSize = 11.sp,
                                            color = colors.textSecondary
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${selectedMonthDay.totalDosePercent.toInt()}%",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = getDoseColor(selectedMonthDay.totalDosePercent)
                                        )
                                        Text(
                                            text = "daily limit used",
                                            fontSize = 9.sp,
                                            color = colors.textTertiary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                HourlyPillBarChart(
                                    hourlyData = selectedMonthDay.hourlyData,
                                    selectedIndex = selectedHourIndex,
                                    onSelectHour = { selectedHourIndex = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        SourceDecompositionCard(
                            sources = selectedMonthDay.sources.ifEmpty { todaySources },
                            totalDose = selectedMonthDay.totalDosePercent,
                            title = "${selectedMonthDay.dateLabel.uppercase()} • SOURCE DECOMPOSITION"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TimeframeCapsuleSelector(
    selected: StatsTimeframe,
    onSelect: (StatsTimeframe) -> Unit
) {
    val colors = SoundDoseTheme.colors

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatsTimeframe.values().forEach { tf ->
            val isSelected = selected == tf
            val shape = RoundedCornerShape(50)
            Box(
                modifier = Modifier
                    .weight(1f)
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
                    .clickable { onSelect(tf) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tf.label,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    letterSpacing = 0.2.sp,
                    color = if (isSelected) colors.pillActiveText else colors.pillInactiveText
                )
            }
        }
    }
}

@Composable
fun HourlyPillBarChart(
    hourlyData: List<HourlyDose>,
    selectedIndex: Int,
    onSelectHour: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SoundDoseTheme.colors
    val selectedHour = hourlyData.getOrNull(selectedIndex)

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize().padding(bottom = 24.dp)) {
            val chartW = size.width
            val chartH = size.height
            val barCount = 24
            val barSpacing = chartW / barCount
            val barWidth = barSpacing * 0.58f

            val y100 = chartH * 0.15f
            val y50 = chartH * 0.55f

            drawLine(
                color = colors.cardBorder,
                start = Offset(0f, y100),
                end = Offset(chartW, y100),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = colors.cardBorderSubtle,
                start = Offset(0f, y50),
                end = Offset(chartW, y50),
                strokeWidth = 1.dp.toPx()
            )

            for (i in 0 until barCount) {
                val item = hourlyData[i]
                val maxFraction = 0.40
                val fraction = (item.percentOfLimit / 100.0 / maxFraction).coerceIn(0.06, 0.95).toFloat()
                val barH = chartH * fraction
                val left = i * barSpacing + (barSpacing - barWidth) / 2f
                val top = chartH - barH

                val isSelected = i == selectedIndex
                val pillColor = if (isSelected) {
                    colors.visualizerColor
                } else if (item.percentOfLimit > 20.0) {
                    DoseWarning
                } else if (item.percentOfLimit > 10.0) {
                    DoseCaution
                } else {
                    if (colors.isDark) Color(0xFF14B8A6) else Color(0xFF0D9488)
                }

                val alpha = if (isSelected) 1.0f else 0.75f

                drawRoundRect(
                    color = pillColor.copy(alpha = alpha),
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barH),
                    cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp)
        ) {
            hourlyData.forEachIndexed { idx, _ ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onSelectHour(idx) }
                )
            }
        }

        if (selectedHour != null) {
            val hourFraction = (selectedIndex.toFloat() / 24f).coerceIn(0.05f, 0.82f)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (hourFraction * 260).dp, y = (-2).dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (colors.isDark) Color(0xFFF1F5F9) else Color(0xFF18181B))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${"%02d".format(selectedHour.hour)}:00",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (colors.isDark) Color(0xFF0F172A) else Color(0xFFFFFBEB)
                    )
                    Text(
                        text = "${"%.0f".format(selectedHour.percentOfLimit)}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (colors.isDark) Color(0xFF0F172A) else Color(0xFFFFFBEB)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("00:00", "06:00", "12:00", "18:00", "23:00").forEach { label ->
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = colors.textTertiary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun GpsHotspotCard(spot: GpsHotspot) {
    val colors = SoundDoseTheme.colors
    val categoryColor = Color(spot.category.colorHex)

    SmudgedCard(
        cornerRadius = 16.dp,
        accentGlow = categoryColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(categoryColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = spot.placeName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = "${spot.soundType} • ${spot.coordinates}",
                    fontSize = 10.sp,
                    color = colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${"%.0f".format(spot.limitPercent)}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = categoryColor
                )
                Text(
                    text = "of daily bank",
                    fontSize = 9.sp,
                    color = colors.textTertiary
                )
            }
        }
    }
}

// 7-Day Weekly Chart Built with Strict Equal Columns (Never wraps or crumbles on large fonts)
@Composable
fun WeeklyInteractiveBarChart(
    days: List<WeekDayData>,
    selectedIndex: Int,
    onSelectDay: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SoundDoseTheme.colors
    val maxScale = 120.0

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEachIndexed { idx, day ->
            val isSelected = idx == selectedIndex
            val color = getDoseColor(day.totalDose)
            val fraction = (day.totalDose / maxScale).coerceIn(0.08, 0.95).toFloat()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelectDay(idx) }
                    .padding(horizontal = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Percentage at Top (Clean, max 1 line)
                Text(
                    text = "${day.totalDose.toInt()}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) colors.textPrimary else color,
                    maxLines = 1,
                    softWrap = false
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Vertical Capsule Bar with constant container height
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.cardBackgroundElevated),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.55f)
                            .fillMaxHeight(fraction)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(if (isSelected) colors.visualizerColor else color.copy(alpha = 0.75f))
                            .then(
                                if (isSelected) Modifier.border(1.5.dp, Color.White, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                else Modifier
                            )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Short Day of Week (e.g. "Wed")
                Text(
                    text = day.dayOfWeek,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) colors.textPrimary else colors.textSecondary,
                    maxLines = 1,
                    softWrap = false
                )

                // Day Date (e.g. "04")
                Text(
                    text = day.dayNumber,
                    fontSize = 9.sp,
                    color = colors.textTertiary,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

// 30-Day Monthly Timeline with Tap Detection on Each Bar
@Composable
fun MonthlyTimelineChart(
    days: List<MonthDayData>,
    selectedIndex: Int,
    onSelectDay: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SoundDoseTheme.colors
    val maxScale = 120.0

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val count = days.size
                val spacing = w / count
                val barW = (spacing * 0.65f).coerceAtLeast(3.dp.toPx())

                // 100% threshold dashed line
                val limitY = h * (1f - (100.0 / maxScale).toFloat())
                drawLine(
                    color = DoseDanger.copy(alpha = 0.35f),
                    start = Offset(0f, limitY),
                    end = Offset(w, limitY),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                )

                days.forEachIndexed { i, item ->
                    val frac = (item.totalDosePercent / maxScale).coerceIn(0.06, 0.95).toFloat()
                    val barH = h * frac
                    val left = i * spacing + (spacing - barW) / 2f
                    val top = h - barH
                    val isSelected = i == selectedIndex

                    drawRoundRect(
                        color = if (isSelected) colors.visualizerColor else getDoseColor(item.totalDosePercent).copy(alpha = 0.70f),
                        topLeft = Offset(left, top),
                        size = Size(barW, barH),
                        cornerRadius = CornerRadius(barW / 2f, barW / 2f)
                    )
                }
            }

            // Interactive Click Overlay across all 30 days
            Row(modifier = Modifier.fillMaxSize()) {
                days.forEachIndexed { idx, _ ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onSelectDay(idx) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Week 1", "Week 2", "Week 3", "Week 4").forEach { wk ->
                Text(
                    text = wk,
                    fontSize = 10.sp,
                    color = colors.textTertiary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Image 3 Reproduction: Classified Acoustic Stream ("What made it loud?")
@Composable
fun ClassifiedAcousticStreamCard() {
    val colors = SoundDoseTheme.colors

    SmudgedCard(
        cornerRadius = 20.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "WHAT MADE IT LOUD?",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                color = colors.textPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Waveform Graphic with classified color bursts
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val centerY = h / 2f
                    val barCount = 42
                    val step = w / barCount
                    val barW = step * 0.40f

                    drawLine(
                        color = colors.cardBorderSubtle,
                        start = Offset(0f, centerY),
                        end = Offset(w, centerY),
                        strokeWidth = 1.dp.toPx()
                    )

                    for (i in 0 until barCount) {
                        val isHornBurst = i in 12..17
                        val isDrillBurst = i in 22..27

                        val ampFraction = when {
                            isHornBurst -> 0.88f - (Math.abs(i - 15) * 0.08f)
                            isDrillBurst -> 0.72f - (Math.abs(i - 25) * 0.06f)
                            i in 7..11 || i in 18..21 -> 0.45f
                            else -> 0.24f
                        }

                        val barColor = when {
                            isHornBurst -> Color(0xFFEF4444) // Bright Red Horn Blast
                            isDrillBurst -> Color(0xFFF59E0B) // Amber/Yellow Drilling
                            else -> if (colors.isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                        }

                        val barHalfH = (h * 0.45f) * ampFraction
                        val x = i * step + (step - barW) / 2f

                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(x, centerY - barHalfH),
                            size = Size(barW, barHalfH * 2f),
                            cornerRadius = CornerRadius(barW / 2f, barW / 2f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AMBIENT",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = colors.textTertiary
                )
                Text(
                    text = "HORN BLAST",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color(0xFFEF4444)
                )
                Text(
                    text = "DRILLING",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color(0xFFF59E0B)
                )
                Text(
                    text = "AMBIENT",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = colors.textTertiary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "CLASSIFIED ACOUSTIC STREAM • 18:04 TO 18:22, INNER RING ROAD CORRIDOR",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = colors.textSecondary
            )
        }
    }
}

// Image 2 Reproduction: Segmented Donut & Monospace Source Decomposition with Clickable Slices
@Composable
fun SourceDecompositionCard(
    sources: List<DecompositionSource>,
    totalDose: Double,
    title: String = "TODAY • SOURCE DECOMPOSITION"
) {
    val colors = SoundDoseTheme.colors
    var selectedSliceIndex by remember(sources) { mutableStateOf(0) }
    val activeSlice = sources.getOrElse(selectedSliceIndex.coerceIn(0, sources.size - 1)) {
        sources.firstOrNull() ?: DecompositionSource("NOISE", "NOISE", 100, colors.visualizerColor)
    }

    SmudgedCard(
        cornerRadius = 20.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = colors.textSecondary
                )
                Text(
                    text = "Tap slice to inspect",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.visualizerColor
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Interactive Segmented Donut Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .size(165.dp)
                        .pointerInput(sources) {
                            detectTapGestures { offset ->
                                val cX = size.width / 2f
                                val cY = size.height / 2f
                                val dx = offset.x - cX
                                val dy = offset.y - cY
                                val dist = kotlin.math.sqrt(dx * dx + dy * dy)

                                // Touch detection with generous ring tolerance
                                if (dist in 15f..140f) {
                                    val touchDeg = Math.toDegrees(kotlin.math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                    val angleFromTop = (touchDeg + 90f + 360f) % 360f

                                    var accumulated = 0f
                                    for (i in sources.indices) {
                                        val sweep = (sources[i].percentage / 100f) * 360f
                                        if (angleFromTop >= accumulated && angleFromTop < accumulated + sweep) {
                                            selectedSliceIndex = i
                                            break
                                        }
                                        accumulated += sweep
                                    }
                                }
                            }
                        }
                ) {
                    val baseStrokeW = 20.dp.toPx()
                    val activeStrokeW = 26.dp.toPx()
                    val arcSize = Size(size.width, size.height)
                    var startAng = -90f

                    for (i in sources.indices) {
                        val slice = sources[i]
                        val isSelected = i == selectedSliceIndex
                        val sweep = (slice.percentage / 100f) * 360f
                        val strokeW = if (isSelected) activeStrokeW else baseStrokeW
                        val color = if (isSelected) slice.color else slice.color.copy(alpha = 0.55f)

                        drawArc(
                            color = color,
                            startAngle = startAng,
                            sweepAngle = (sweep - 3f).coerceAtLeast(1f),
                            useCenter = false,
                            style = Stroke(width = strokeW, cap = StrokeCap.Round),
                            size = arcSize
                        )
                        startAng += sweep
                    }
                }

                // Dynamic Donut Center Text showing tapped slice's data
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 14.dp)
                ) {
                    Text(
                        text = "${activeSlice.percentage}%",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        color = activeSlice.color
                    )
                    Text(
                        text = activeSlice.shortLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.8.sp,
                        color = colors.textPrimary
                    )
                }
            }

            // Detailed Description for active slice
            if (activeSlice.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(activeSlice.color.copy(alpha = 0.10f))
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(activeSlice.color)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = activeSlice.description,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Clean Monospace Legend List with Clickable Rows and Active Highlight
            sources.forEachIndexed { index, slice ->
                val isSelected = index == selectedSliceIndex
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .then(
                            if (isSelected) Modifier.background(colors.cardBackgroundElevated)
                            else Modifier
                        )
                        .clickable { selectedSliceIndex = index }
                        .padding(horizontal = 10.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 10.dp else 7.dp)
                                .clip(CircleShape)
                                .background(slice.color)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = slice.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = if (isSelected) colors.textPrimary else slice.color
                        )
                    }
                    Text(
                        text = "${slice.percentage}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) slice.color else colors.textPrimary
                    )
                }

                if (index < sources.size - 1) {
                    HorizontalDivider(
                        color = colors.cardBorderSubtle,
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}
