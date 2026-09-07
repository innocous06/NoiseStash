package com.sounddose.data

import com.sounddose.dose.NoiseCategory
import java.time.LocalDate

object SeedData {

    fun generateDemoData(): List<DoseEntry> {
        val entries = mutableListOf<DoseEntry>()
        val today = LocalDate.now()

        // 6 historical days for delivery rider persona
        val dayProfiles = listOf(
            Triple(today.minusDays(6), 92.0, NoiseCategory.TRAFFIC),
            Triple(today.minusDays(5), 78.0, NoiseCategory.TRAFFIC),
            Triple(today.minusDays(4), 105.0, NoiseCategory.HORN),
            Triple(today.minusDays(3), 64.0, NoiseCategory.TRAFFIC),
            Triple(today.minusDays(2), 88.0, NoiseCategory.CONSTRUCTION),
            Triple(today.minusDays(1), 18.0, NoiseCategory.AMBIENT)
        )

        for ((date, targetDose, dominant) in dayProfiles) {
            val dateStr = date.toString()
            val numEntries = 20

            val categories = listOf(
                dominant to 0.55,
                NoiseCategory.AMBIENT to 0.20,
                NoiseCategory.HORN to 0.15,
                NoiseCategory.MUSIC to 0.10
            )

            val baseTime = System.currentTimeMillis() - ((today.toEpochDay() - date.toEpochDay()) * 86400000L)

            for (i in 0 until numEntries) {
                val (cat, weight) = categories[i % categories.size]
                val doseChunk = (targetDose * weight / (numEntries * weight / categories.size))
                val db = when {
                    doseChunk > 2.0 -> 94.0 + (Math.random() * 8.0)
                    doseChunk > 0.5 -> 86.0 + (Math.random() * 6.0)
                    else -> 68.0 + (Math.random() * 12.0)
                }

                entries.add(
                    DoseEntry(
                        timestamp = baseTime + (i * 1800000L),
                        date = dateStr,
                        dbLevel = db,
                        doseIncrement = doseChunk,
                        category = cat.name,
                        rawLabel = cat.label,
                        confidence = (0.75 + Math.random() * 0.2).toFloat(),
                        locationTag = if (i % 2 == 0) "Outer Ring Road" else "Koramangala 80ft Rd"
                    )
                )
            }
        }
        return entries
    }
}
