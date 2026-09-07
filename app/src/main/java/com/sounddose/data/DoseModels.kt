package com.sounddose.data

data class DoseEntry(
    val id: Long = 0,
    val timestamp: Long,
    val date: String,
    val dbLevel: Double,
    val doseIncrement: Double,
    val category: String,
    val rawLabel: String,
    val confidence: Float,
    val locationTag: String? = null
)

data class CategoryDose(
    val category: String,
    val totalDose: Double
)

data class DailyDoseSummary(
    val date: String,
    val totalDose: Double,
    val entryCount: Int
)
